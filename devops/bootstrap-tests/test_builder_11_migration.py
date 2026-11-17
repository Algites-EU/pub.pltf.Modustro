"""Builder 1.1 dependency and descriptor-driven universal bootstrap regressions."""
from __future__ import annotations
import hashlib
import json
from pathlib import Path
import subprocess
import yaml

ROOT = Path(__file__).resolve().parents[2]
BUILDER = ROOT / 'platform/builder'
VERSION = '1.1-SNAPSHOT'


def test_repository_version():
    doc = yaml.safe_load((ROOT / 'modustro-source-repository.yml').read_text())
    assert doc['Version'] == {'ReleaseLineVersion':'1', 'Revision':1, 'QualifierKind':'snapshot'}


def test_versioned_dependencies_and_no_governance_1_0():
    for path in (BUILDER/'core/impl/modustro-artifact.yml',
                 BUILDER/'gradle/modustro-artifact.yml',
                 BUILDER/'runtime/modustro-artifact.yml'):
        doc = yaml.safe_load(path.read_text())
        for block in doc.get('Dependencies') or []:
            if block.get('DependencyKind') != 'modustro':
                continue
            for dep in block.get('Items') or []:
                assert dep.get('VersionRequirement', {}).get('Exact', VERSION) == VERSION, (path, dep)
                assert 'pub.gov.Algites_devops.build.modustro' not in dep['ArtifactId']


def test_runtime_dependencies_are_descriptor_driven():
    doc = yaml.safe_load((BUILDER/'runtime/modustro-artifact.yml').read_text())
    deps = next(x['Items'] for x in doc['Dependencies'] if x['DependencyKind']=='modustro')
    assert len(deps) >= 15
    assert any(x['ArtifactId']=='pub.pltf.Modustro_platform.builder.core.impl' for x in deps)
    assert all(x['VersionRequirement']['Exact']==VERSION for x in deps)
    source = (ROOT/'devops/bootstrap/prepare.py').read_text()
    assert 'def module_records(' in source
    assert 'technology_kinds(' in source
    assert 'pub.gov.Algites_devops.build.modustrobuild' not in (ROOT/'devops/bootstrap/build.sh').read_text()


def test_gradle_plugin_exports_correct_runtime():
    gradle = (BUILDER/'gradle/build.gradle.kts').read_text()
    settings = (ROOT/'settings.gradle.kts').read_text()
    descriptor = yaml.safe_load((BUILDER/'gradle/modustro-artifact.yml').read_text())
    assert 'pub.pltf.Modustro_platform.builder.runtime:1.1-SNAPSHOT' in gradle
    assert 'pub.pltf.Modustro_platform.builder.core.intf:1.1-SNAPSHOT' in gradle
    assert 'pub.pltf.Modustro_platform.builder.gradle:1.1-SNAPSHOT' in settings
    dependencies = [dep for group in descriptor.get('Dependencies') or []
                    if group['DependencyKind']=='modustro' for dep in group['Items']
                    if dep['ArtifactId']=='pub.pltf.Modustro_platform.builder.runtime']
    assert len(dependencies)==1
    assert dependencies[0]['GroupId']=='eu.algites.pltf.modustro.builder'
    assert dependencies[0].get('VersionRequirement', {}).get('Exact', VERSION)==VERSION


def test_bundled_governance_defaults_unchanged():
    resource=BUILDER/'gradle/src/product/resources/algites-repository-defaults-public.yml'
    data=resource.read_bytes()
    blob=hashlib.sha1(b'blob '+str(len(data)).encode()+b'\x00'+data).hexdigest()
    assert blob=='b45d198f262693635fa4e51e1cb7e7d8023636e4'


def test_universal_bootstrap_plans_all_four_artifacts():
    for name in ('bootstrap.sh','build.sh','generate.sh','upload-cloudsmith.sh'):
        subprocess.run(['bash','-n',str(ROOT/'devops/bootstrap'/name)],check=True)
    result=subprocess.run(['bash',str(ROOT/'devops/bootstrap/bootstrap.sh'),
                           '--plan-only','--root','platform/builder'],
                          cwd=ROOT,capture_output=True,text=True,check=True)
    assert '4 Java Maven and 1 Python distributions' in result.stdout, result.stdout
    modules=json.loads((ROOT/'build/run/bootstrap/artifacts.json').read_text())
    identities={record['artifactId'] for record in modules}
    assert identities=={f'pub.pltf.Modustro_platform.builder.{name}' for name in
                        ('core.intf','core.impl','runtime','gradle')}
