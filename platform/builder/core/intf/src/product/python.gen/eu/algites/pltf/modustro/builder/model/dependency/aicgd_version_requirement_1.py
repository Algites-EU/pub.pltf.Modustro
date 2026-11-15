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
class AIcgdVersionRequirement_1:
    """Canonical representations: YAMLDEFS: urn:algites:modustro:builder:yamldef:model:version-requirement:1 (src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json); JSONDEFS: urn:algites:modustro:builder:jsondef:model:version-requirement:1 (src/product/jsondefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.jsondef.schema.json)

    Generated from canonical definition urn:algites:modustro:builder:yamldef:model:version-requirement:1/1. Source: src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json. Do not edit manually.

    Attributes:
        _schema: Optional absolute URI of the schema describing this root document.
        exact: Value of canonical property Exact.
        minimum: Value of canonical property Minimum.
        maximum: Value of canonical property Maximum.
        maximum_strict: Value of canonical property MaximumStrict.
        exclude: Value of canonical property Exclude.
        prefer: Value of canonical property Prefer.
    """
    __canonical_source_id__ = "urn:algites:modustro:builder:yamldef:model:version-requirement:1"
    __canonical_source_version__ = 1
    __canonical_source_resource__ = "src/product/yamldefs/eu/algites/pltf/modustro/builder/model/dependency/version-requirement_1.yamldef.schema.json"

    SCHEMA_FIELD_NAME__SCHEMA = "$schema"
    """**Field Name:** ``$schema``

    **Field Description:** Optional absolute URI of the schema describing this root document."""
    SCHEMA_FIELD_NAME__EXACT = "Exact"
    """**Field Name:** ``Exact``"""
    SCHEMA_FIELD_NAME__MINIMUM = "Minimum"
    """**Field Name:** ``Minimum``"""
    SCHEMA_FIELD_NAME__MAXIMUM = "Maximum"
    """**Field Name:** ``Maximum``"""
    SCHEMA_FIELD_NAME__MAXIMUM_STRICT = "MaximumStrict"
    """**Field Name:** ``MaximumStrict``"""
    SCHEMA_FIELD_NAME__EXCLUDE = "Exclude"
    """**Field Name:** ``Exclude``"""
    SCHEMA_FIELD_NAME__PREFER = "Prefer"
    """**Field Name:** ``Prefer``"""

    _schema: str = _AI_UNSET
    exact: str | None = _AI_UNSET
    minimum: Any | None = _AI_UNSET
    maximum: Any | None = _AI_UNSET
    maximum_strict: bool | None = _AI_UNSET
    exclude: Any | None = _AI_UNSET
    prefer: str | None = _AI_UNSET

    def __post_init__(self):
        if self._schema is not _AI_UNSET:
            if self._schema is None:
                raise ValueError("$schema" + ": null is not permitted")
            else:
                _ai_validate(self._schema, "STRING", ("string", None, None, False, False, None, None, None, ()), "$schema")
        if self.exact is not _AI_UNSET:
            if self.exact is None:
                pass
            else:
                _ai_validate(self.exact, "STRING", ("string", None, None, False, False, 1, None, None, ()), "Exact")
        if self.minimum is not _AI_UNSET:
            if self.minimum is None:
                pass
            else:
                _ai_validate(self.minimum, "ANY", (None, None, None, False, False, None, None, None, ()), "Minimum")
        if self.maximum is not _AI_UNSET:
            if self.maximum is None:
                pass
            else:
                _ai_validate(self.maximum, "ANY", (None, None, None, False, False, None, None, None, ()), "Maximum")
        if self.maximum_strict is not _AI_UNSET:
            if self.maximum_strict is None:
                pass
            else:
                _ai_validate(self.maximum_strict, "BOOLEAN", ("boolean", None, None, False, False, None, None, None, ()), "MaximumStrict")
        if self.exclude is not _AI_UNSET:
            if self.exclude is None:
                pass
            else:
                _ai_validate(self.exclude, "ANY", (None, None, None, False, False, None, None, None, ()), "Exclude")
        if self.prefer is not _AI_UNSET:
            if self.prefer is None:
                pass
            else:
                _ai_validate(self.prefer, "STRING", ("string", None, None, False, False, 1, None, None, ()), "Prefer")

    @classmethod
    def from_mapping(cls, value: Mapping[str, object]):
        return cls(_schema=(value.get("$schema", _AI_UNSET) if value.get("$schema", _AI_UNSET) is _AI_UNSET or value.get("$schema", _AI_UNSET) is None else _ai_convert(value.get("$schema", _AI_UNSET), "string", "STRING")), exact=(value.get("Exact", _AI_UNSET) if value.get("Exact", _AI_UNSET) is _AI_UNSET or value.get("Exact", _AI_UNSET) is None else _ai_convert(value.get("Exact", _AI_UNSET), "string", "STRING")), minimum=(value.get("Minimum", _AI_UNSET) if value.get("Minimum", _AI_UNSET) is _AI_UNSET or value.get("Minimum", _AI_UNSET) is None else _ai_convert(value.get("Minimum", _AI_UNSET), None, "ANY")), maximum=(value.get("Maximum", _AI_UNSET) if value.get("Maximum", _AI_UNSET) is _AI_UNSET or value.get("Maximum", _AI_UNSET) is None else _ai_convert(value.get("Maximum", _AI_UNSET), None, "ANY")), maximum_strict=(value.get("MaximumStrict", _AI_UNSET) if value.get("MaximumStrict", _AI_UNSET) is _AI_UNSET or value.get("MaximumStrict", _AI_UNSET) is None else _ai_convert(value.get("MaximumStrict", _AI_UNSET), "boolean", "BOOLEAN")), exclude=(value.get("Exclude", _AI_UNSET) if value.get("Exclude", _AI_UNSET) is _AI_UNSET or value.get("Exclude", _AI_UNSET) is None else _ai_convert(value.get("Exclude", _AI_UNSET), None, "ANY")), prefer=(value.get("Prefer", _AI_UNSET) if value.get("Prefer", _AI_UNSET) is _AI_UNSET or value.get("Prefer", _AI_UNSET) is None else _ai_convert(value.get("Prefer", _AI_UNSET), "string", "STRING")))

    def to_mapping(self) -> Mapping[str, object]:
        return {key: value for key, value in {
            "$schema": _ai_wire(self._schema, "string"),
            "Exact": _ai_wire(self.exact, "string"),
            "Minimum": _ai_wire(self.minimum, None),
            "Maximum": _ai_wire(self.maximum, None),
            "MaximumStrict": _ai_wire(self.maximum_strict, "boolean"),
            "Exclude": _ai_wire(self.exclude, None),
            "Prefer": _ai_wire(self.prefer, "string"),
        }.items() if value is not _AI_UNSET}
