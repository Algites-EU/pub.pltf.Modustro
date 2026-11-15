from __future__ import annotations

from typing import Any, Mapping
"""Standalone support embedded into generated Python modules; no generator runtime dependency."""
from dataclasses import dataclass
from decimal import Decimal
import base64
import calendar
import re
import struct

_AI_UNSET = object()


@dataclass(frozen=True, slots=True)
class _AIcSchemaTemporalValue:
    """An XML calendar value retaining arbitrary years, precision and optional timezone."""
    lexical: str
    data_type: str

    def __post_init__(self):
        patterns = {
            'date': r'(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})',
            'dateTime': r'(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2}(?:\.[0-9]+)?)',
            'dateTimeStamp': r'(-?[0-9]{4,})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2}(?:\.[0-9]+)?)',
            'time': r'([0-9]{2}):([0-9]{2}):([0-9]{2}(?:\.[0-9]+)?)',
            'gYearMonth': r'(-?[0-9]{4,})-([0-9]{2})', 'gYear': r'(-?[0-9]{4,})',
            'gMonthDay': r'--([0-9]{2})-([0-9]{2})', 'gDay': r'---([0-9]{2})', 'gMonth': r'--([0-9]{2})(?:--)?',
        }
        match = re.fullmatch(patterns[self.data_type]+r'(Z|[+-][0-9]{2}:[0-9]{2})?', self.lexical)
        if not match: raise ValueError(f'Invalid {self.data_type}: {self.lexical}')
        groups = list(match.groups()); timezone = groups.pop()
        if self.data_type == 'dateTimeStamp' and timezone is None: raise ValueError('dateTimeStamp requires a timezone')
        if timezone and timezone != 'Z':
            hours, minutes = map(int, timezone[1:].split(':'))
            if hours > 14 or minutes > 59 or (hours == 14 and minutes): raise ValueError('Invalid timezone offset')
        if self.data_type in ('date', 'dateTime', 'dateTimeStamp', 'gYearMonth', 'gYear'):
            year = int(groups[0])
            if year == 0: raise ValueError('XSD 1.0 has no year zero')
            if len(groups) >= 2 and not 1 <= int(groups[1]) <= 12: raise ValueError('Invalid month')
            if len(groups) >= 3 and not 1 <= int(groups[2]) <= calendar.monthrange(year, int(groups[1]))[1]: raise ValueError('Invalid calendar date')
        if self.data_type == 'gMonthDay':
            month, day = map(int, groups)
            if not 1 <= month <= 12 or not 1 <= day <= calendar.monthrange(2000, month)[1]: raise ValueError('Invalid month/day')
        if self.data_type == 'gDay' and not 1 <= int(groups[0]) <= 31: raise ValueError('Invalid day')
        if self.data_type == 'gMonth' and not 1 <= int(groups[0]) <= 12: raise ValueError('Invalid month')
        clock = groups[3:] if self.data_type in ('dateTime', 'dateTimeStamp') else groups if self.data_type == 'time' else None
        if clock:
            hour, minute, second = int(clock[0]), int(clock[1]), Decimal(clock[2])
            if not 0 <= hour <= 24 or not 0 <= minute < 60 or not 0 <= second < 60 or (hour == 24 and (minute or second)): raise ValueError('Invalid clock time')


@dataclass(frozen=True, slots=True)
class _AIcSchemaDuration:
    """A calendar duration preserving months and exact fractional seconds."""
    lexical: str

    def __post_init__(self):
        match = re.fullmatch(r'-?P(?:(\d+)Y)?(?:(\d+)M)?(?:(\d+)D)?(?:T(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?)?', self.lexical)
        if not match or not any(value is not None for value in match.groups()) or self.lexical.endswith('T'): raise ValueError('Invalid XML duration')


# Public aliases are embedded into generated modules; implementations stay private.
AIcSchemaTemporalValue = _AIcSchemaTemporalValue
AIcSchemaDuration = _AIcSchemaDuration

def _ai_convert(value, data_type, kind):
    if value is None: return None
    if data_type in ('hexBinary', 'base64Binary'):
        if isinstance(value, bytes): return value
        if not isinstance(value, str): raise TypeError('Binary value must be bytes or encoded text')
        return bytes.fromhex(value) if data_type == 'hexBinary' else base64.b64decode(value, validate=True)
    if data_type in ('date', 'time', 'dateTime', 'dateTimeStamp', 'gYearMonth', 'gYear', 'gMonthDay', 'gDay', 'gMonth'):
        return value if isinstance(value, AIcSchemaTemporalValue) else AIcSchemaTemporalValue(str(value), data_type)
    if data_type in ('duration', 'yearMonthDuration', 'dayTimeDuration'):
        return value if isinstance(value, AIcSchemaDuration) else AIcSchemaDuration(str(value))
    if kind == 'INTEGER':
        if isinstance(value, bool): raise TypeError('Boolean is not an integer value')
        if isinstance(value, int): return value
        if isinstance(value, str) and re.fullmatch(r'[+-]?\d+', value): return int(value)
        raise TypeError('Expected an exact integer')
    if kind == 'NUMBER':
        if isinstance(value, bool): raise TypeError('Boolean is not a numeric value')
        if data_type == 'float': return struct.unpack('!f', struct.pack('!f', float(value)))[0]
        return float(value) if data_type == 'double' else Decimal(str(value))
    return value


def _ai_validate(value, kind, constraints, name):
    data_type, minimum, maximum, exclusive_min, exclusive_max, min_len, max_len, pattern, values = constraints
    if kind == 'INTEGER' and (not isinstance(value, int) or isinstance(value, bool)): raise TypeError(f'{name}: expected int')
    if kind == 'NUMBER' and not isinstance(value, float if data_type in ('float', 'double') else Decimal): raise TypeError(f'{name}: incorrect numeric representation')
    if kind == 'BOOLEAN' and not isinstance(value, bool): raise TypeError(f'{name}: expected bool')
    if kind == 'STRING':
        expected = bytes if data_type in ('hexBinary', 'base64Binary') else AIcSchemaTemporalValue if data_type in ('date', 'time', 'dateTime', 'dateTimeStamp', 'gYearMonth', 'gYear', 'gMonthDay', 'gDay', 'gMonth') else AIcSchemaDuration if data_type in ('duration', 'yearMonthDuration', 'dayTimeDuration') else str
        if not isinstance(value, expected): raise TypeError(f'{name}: incorrect scalar representation')
        if isinstance(value, AIcSchemaTemporalValue) and value.data_type != data_type: raise ValueError(f'{name}: incorrect temporal type')
    if kind == 'OBJECT' and not isinstance(value, dict):
        from collections.abc import Mapping
        if not isinstance(value, Mapping): raise TypeError(f'{name}: expected mapping')
    if data_type in ('anySimpleType', 'anyAtomicType') and isinstance(value, (dict, list, tuple)): raise TypeError(f'{name}: expected scalar')
    if minimum is not None or maximum is not None:
        numeric = Decimal(str(value))
        if minimum is not None and (numeric < Decimal(minimum) or (exclusive_min and numeric == Decimal(minimum))): raise ValueError(f'{name}: below minimum')
        if maximum is not None and (numeric > Decimal(maximum) or (exclusive_max and numeric == Decimal(maximum))): raise ValueError(f'{name}: above maximum')
    if min_len is not None and len(value) < min_len: raise ValueError(f'{name}: too short')
    if max_len is not None and len(value) > max_len: raise ValueError(f'{name}: too long')
    if pattern is not None and re.search(pattern, str(value)) is None: raise ValueError(f'{name}: pattern mismatch')
    if values and str(value) not in values: raise ValueError(f'{name}: invalid enum value')


def _ai_wire(value, data_type=None):
    if value is _AI_UNSET: return value
    if value is None: return None
    if isinstance(value, bytes): return value.hex().upper() if data_type == 'hexBinary' else base64.b64encode(value).decode('ascii')
    if isinstance(value, (AIcSchemaTemporalValue, AIcSchemaDuration)): return value.lexical
    if isinstance(value, (tuple, list)): return [_ai_wire(item, data_type) for item in value]
    if hasattr(value, 'to_mapping'): return value.to_mapping()
    from enum import Enum
    if isinstance(value, Enum): return value.value
    return value



@dataclass(frozen=True, slots=True)
class AIcgdGlobalPublicationDeployMetadata_1:
    """Deployment-controlled publication metadata generated from validated user metadata plus trusted publisher state. This document is not an author input contract. Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:publication:global-publication-deploy-metadata:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.jsondef.schema.json)

    Generated from canonical definition urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json. Do not edit manually.

    Attributes:
        global_publication_path_id: Stable logical publication path copied from validated user metadata.
        publication_state: Server-controlled publication lifecycle state. Released definitions are immutable and cannot transition back to draft.
        publication_revision: Server-controlled monotonically increasing revision of successfully deployed draft content. Release freezes the current content revision.
        first_published_at: Server-generated timestamp of the first successful deployment for this GlobalPublicationPathId.
        published_at: Server-generated timestamp of the most recent successful deployment or release transition represented by this metadata.
        content_sha256: SHA-256 of the exact deployed canonical content, used to reject incomplete or inconsistent definition/sidecar pairs.
        _schema: Optional absolute URI of the schema describing this root document.
        released_at: Server-generated timestamp at which this publication became an immutable release; absent while the publication is a draft.
        published_by: Optional trusted publisher identity derived by the deployment adapter from authenticated execution context; never copied from user metadata.
    """
    __canonical_source_id__ = "urn:algites:modustro:builder:yamldef:publication:global-publication-deploy-metadata:1"
    __canonical_source_version__ = 1
    __canonical_source_resource__ = "src/product/yamldefs/eu/algites/pltf/modustro/builder/publication/global-publication-deploy-metadata_1.yamldef.schema.json"

    SCHEMA_FIELD_NAME__GLOBAL_PUBLICATION_PATH_ID = "GlobalPublicationPathId"
    """**Field Name:** ``GlobalPublicationPathId``

    **Field Description:** Stable logical publication path copied from validated user metadata."""
    SCHEMA_FIELD_NAME__PUBLICATION_STATE = "PublicationState"
    """**Field Name:** ``PublicationState``

    **Field Description:** Server-controlled publication lifecycle state. Released definitions are immutable and cannot transition back to draft."""
    SCHEMA_FIELD_NAME__PUBLICATION_REVISION = "PublicationRevision"
    """**Field Name:** ``PublicationRevision``

    **Field Description:** Server-controlled monotonically increasing revision of successfully deployed draft content. Release freezes the current content revision."""
    SCHEMA_FIELD_NAME__FIRST_PUBLISHED_AT = "FirstPublishedAt"
    """**Field Name:** ``FirstPublishedAt``

    **Field Description:** Server-generated timestamp of the first successful deployment for this GlobalPublicationPathId."""
    SCHEMA_FIELD_NAME__PUBLISHED_AT = "PublishedAt"
    """**Field Name:** ``PublishedAt``

    **Field Description:** Server-generated timestamp of the most recent successful deployment or release transition represented by this metadata."""
    SCHEMA_FIELD_NAME__CONTENT_SHA256 = "ContentSha256"
    """**Field Name:** ``ContentSha256``

    **Field Description:** SHA-256 of the exact deployed canonical content, used to reject incomplete or inconsistent definition/sidecar pairs."""
    SCHEMA_FIELD_NAME__SCHEMA = "$schema"
    """**Field Name:** ``$schema``

    **Field Description:** Optional absolute URI of the schema describing this root document."""
    SCHEMA_FIELD_NAME__RELEASED_AT = "ReleasedAt"
    """**Field Name:** ``ReleasedAt``

    **Field Description:** Server-generated timestamp at which this publication became an immutable release; absent while the publication is a draft."""
    SCHEMA_FIELD_NAME__PUBLISHED_BY = "PublishedBy"
    """**Field Name:** ``PublishedBy``

    **Field Description:** Optional trusted publisher identity derived by the deployment adapter from authenticated execution context; never copied from user metadata."""

    global_publication_path_id: str
    publication_state: str
    publication_revision: int
    first_published_at: AIcSchemaTemporalValue
    published_at: AIcSchemaTemporalValue
    content_sha256: str
    _schema: str = _AI_UNSET
    released_at: AIcSchemaTemporalValue = _AI_UNSET
    published_by: str = _AI_UNSET

    def __post_init__(self):
        if self.global_publication_path_id is not _AI_UNSET:
            if self.global_publication_path_id is None:
                raise ValueError("GlobalPublicationPathId" + ": null is not permitted")
            else:
                _ai_validate(self.global_publication_path_id, "STRING", ("string", None, None, False, False, 1, None, "^(?!/)(?!.*(?:^|/)\\.\\.(?:/|$))(?!.*//)[A-Za-z0-9._/-]+$", ()), "GlobalPublicationPathId")
        else: raise ValueError("GlobalPublicationPathId" + ": value is required")
        if self.publication_state is not _AI_UNSET:
            if self.publication_state is None:
                raise ValueError("PublicationState" + ": null is not permitted")
            else:
                _ai_validate(self.publication_state, "STRING", ("string", None, None, False, False, None, None, None, ("draft", "release",)), "PublicationState")
        else: raise ValueError("PublicationState" + ": value is required")
        if self.publication_revision is not _AI_UNSET:
            if self.publication_revision is None:
                raise ValueError("PublicationRevision" + ": null is not permitted")
            else:
                _ai_validate(self.publication_revision, "INTEGER", ("integer", "1", None, False, False, None, None, None, ()), "PublicationRevision")
        else: raise ValueError("PublicationRevision" + ": value is required")
        if self.first_published_at is not _AI_UNSET:
            if self.first_published_at is None:
                raise ValueError("FirstPublishedAt" + ": null is not permitted")
            else:
                _ai_validate(self.first_published_at, "STRING", ("dateTime", None, None, False, False, None, None, None, ()), "FirstPublishedAt")
        else: raise ValueError("FirstPublishedAt" + ": value is required")
        if self.published_at is not _AI_UNSET:
            if self.published_at is None:
                raise ValueError("PublishedAt" + ": null is not permitted")
            else:
                _ai_validate(self.published_at, "STRING", ("dateTime", None, None, False, False, None, None, None, ()), "PublishedAt")
        else: raise ValueError("PublishedAt" + ": value is required")
        if self.content_sha256 is not _AI_UNSET:
            if self.content_sha256 is None:
                raise ValueError("ContentSha256" + ": null is not permitted")
            else:
                _ai_validate(self.content_sha256, "STRING", ("string", None, None, False, False, None, None, "^[a-f0-9]{64}$", ()), "ContentSha256")
        else: raise ValueError("ContentSha256" + ": value is required")
        if self._schema is not _AI_UNSET:
            if self._schema is None:
                raise ValueError("$schema" + ": null is not permitted")
            else:
                _ai_validate(self._schema, "STRING", ("string", None, None, False, False, None, None, None, ()), "$schema")
        if self.released_at is not _AI_UNSET:
            if self.released_at is None:
                raise ValueError("ReleasedAt" + ": null is not permitted")
            else:
                _ai_validate(self.released_at, "STRING", ("dateTime", None, None, False, False, None, None, None, ()), "ReleasedAt")
        if self.published_by is not _AI_UNSET:
            if self.published_by is None:
                raise ValueError("PublishedBy" + ": null is not permitted")
            else:
                _ai_validate(self.published_by, "STRING", ("string", None, None, False, False, 1, None, None, ()), "PublishedBy")

    @classmethod
    def from_mapping(cls, value: Mapping[str, object]):
        return cls(global_publication_path_id=(value["GlobalPublicationPathId"] if value["GlobalPublicationPathId"] is _AI_UNSET or value["GlobalPublicationPathId"] is None else _ai_convert(value["GlobalPublicationPathId"], "string", "STRING")), publication_state=(value["PublicationState"] if value["PublicationState"] is _AI_UNSET or value["PublicationState"] is None else _ai_convert(value["PublicationState"], "string", "STRING")), publication_revision=(value["PublicationRevision"] if value["PublicationRevision"] is _AI_UNSET or value["PublicationRevision"] is None else _ai_convert(value["PublicationRevision"], "integer", "INTEGER")), first_published_at=(value["FirstPublishedAt"] if value["FirstPublishedAt"] is _AI_UNSET or value["FirstPublishedAt"] is None else _ai_convert(value["FirstPublishedAt"], "dateTime", "STRING")), published_at=(value["PublishedAt"] if value["PublishedAt"] is _AI_UNSET or value["PublishedAt"] is None else _ai_convert(value["PublishedAt"], "dateTime", "STRING")), content_sha256=(value["ContentSha256"] if value["ContentSha256"] is _AI_UNSET or value["ContentSha256"] is None else _ai_convert(value["ContentSha256"], "string", "STRING")), _schema=(value.get("$schema", _AI_UNSET) if value.get("$schema", _AI_UNSET) is _AI_UNSET or value.get("$schema", _AI_UNSET) is None else _ai_convert(value.get("$schema", _AI_UNSET), "string", "STRING")), released_at=(value.get("ReleasedAt", _AI_UNSET) if value.get("ReleasedAt", _AI_UNSET) is _AI_UNSET or value.get("ReleasedAt", _AI_UNSET) is None else _ai_convert(value.get("ReleasedAt", _AI_UNSET), "dateTime", "STRING")), published_by=(value.get("PublishedBy", _AI_UNSET) if value.get("PublishedBy", _AI_UNSET) is _AI_UNSET or value.get("PublishedBy", _AI_UNSET) is None else _ai_convert(value.get("PublishedBy", _AI_UNSET), "string", "STRING")))

    def to_mapping(self) -> Mapping[str, object]:
        return {key: value for key, value in {
            "GlobalPublicationPathId": _ai_wire(self.global_publication_path_id, "string"),
            "PublicationState": _ai_wire(self.publication_state, "string"),
            "PublicationRevision": _ai_wire(self.publication_revision, "integer"),
            "FirstPublishedAt": _ai_wire(self.first_published_at, "dateTime"),
            "PublishedAt": _ai_wire(self.published_at, "dateTime"),
            "ContentSha256": _ai_wire(self.content_sha256, "string"),
            "$schema": _ai_wire(self._schema, "string"),
            "ReleasedAt": _ai_wire(self.released_at, "dateTime"),
            "PublishedBy": _ai_wire(self.published_by, "string"),
        }.items() if value is not _AI_UNSET}
