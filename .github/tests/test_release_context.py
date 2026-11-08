"""Exercise release guards against real, isolated Git histories."""

import os
from pathlib import Path
import subprocess
import tempfile
import unittest


SCRIPT = Path(__file__).resolve().parents[1] / "scripts" / "release-context.sh"


class ReleaseContextTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="ksef-release-test-")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.output = self.root / "github-env"
        self.env = dict(os.environ)
        for key in ("RELEASE_TAG", "RELEASE_VERSION", "GITHUB_EVENT_NAME", "GITHUB_REF"):
            self.env.pop(key, None)
        self.env.update(
            GIT_CONFIG_GLOBAL=os.devnull,
            GIT_CONFIG_NOSYSTEM="1",
            GIT_AUTHOR_NAME="Release guard test",
            GIT_AUTHOR_EMAIL="test@example.invalid",
            GIT_COMMITTER_NAME="Release guard test",
            GIT_COMMITTER_EMAIL="test@example.invalid",
            GITHUB_ENV=str(self.output),
        )
        self.git("init", "-q", "-b", "main")
        self.commit("initial")
        self.git("update-ref", "refs/remotes/origin/main", "HEAD")

    def git(self, *args):
        return subprocess.check_output(
            ["git", *args], cwd=self.root, env=self.env, text=True, stderr=subprocess.STDOUT
        ).strip()

    def commit(self, text):
        (self.root / "source.txt").write_text(text)
        self.git("add", "source.txt")
        self.git("commit", "-qm", text)

    def invoke(self, mode="publish", success=True, **values):
        env = dict(self.env, **values)
        result = subprocess.run(
            ["bash", str(SCRIPT), mode], cwd=self.root, env=env,
            text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        )
        if success:
            self.assertEqual(result.returncode, 0, result.stdout)
            self.assertIn("BUILD_TIMESTAMP=", self.output.read_text())
        else:
            self.assertNotEqual(result.returncode, 0, result.stdout)
            self.assertIn("::error::", result.stdout)
            self.assertFalse(self.output.exists(), "Rejected input must not write GITHUB_ENV")
        return result

    def test_annotated_tag_on_main_can_publish(self):
        self.git("tag", "-a", "v2.3.16", "-m", "Release")
        self.invoke(RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="push", GITHUB_REF="refs/tags/v2.3.16")
        self.assertIn("VERSION=2.3.16\n", self.output.read_text())

    def test_older_main_commit_can_publish(self):
        self.git("tag", "v2.3.16")
        self.commit("main advanced")
        self.git("update-ref", "refs/remotes/origin/main", "HEAD")
        self.git("checkout", "-q", "v2.3.16")
        self.invoke(RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="push", GITHUB_REF="refs/tags/v2.3.16")

    def test_tag_outside_main_cannot_publish(self):
        self.git("checkout", "-qb", "feature")
        self.commit("unreviewed change")
        self.git("tag", "v2.3.16")
        self.invoke(success=False, RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="push", GITHUB_REF="refs/tags/v2.3.16")

    def test_mismatched_checkout_cannot_publish(self):
        self.git("tag", "v2.3.16")
        self.commit("different checkout")
        self.git("update-ref", "refs/remotes/origin/main", "HEAD")
        self.invoke(success=False, RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="push", GITHUB_REF="refs/tags/v2.3.16")

    def test_manual_publication_requires_main_workflow(self):
        self.git("tag", "v2.3.16")
        self.invoke(success=False, RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="workflow_dispatch", GITHUB_REF="refs/heads/feature")
        self.invoke(RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="workflow_dispatch", GITHUB_REF="refs/heads/main")

    def test_publish_requires_tag(self):
        self.invoke(success=False, RELEASE_VERSION="2.3.16", GITHUB_EVENT_NAME="workflow_dispatch", GITHUB_REF="refs/heads/main")

    def test_event_must_match_tag(self):
        self.git("tag", "v2.3.16")
        self.invoke(success=False, RELEASE_TAG="v2.3.16", GITHUB_EVENT_NAME="push", GITHUB_REF="refs/tags/v2.3.17")

    def test_dry_run_accepts_untagged_feature_branch(self):
        self.git("checkout", "-qb", "feature")
        self.commit("release configuration experiment")
        self.invoke("dry-run", RELEASE_VERSION="2.4.0-rc.1")
        self.assertIn("VERSION=2.4.0-rc.1\n", self.output.read_text())

    def test_dry_run_can_derive_version_from_tag(self):
        self.git("tag", "v2.3.16")
        self.invoke("dry-run", RELEASE_TAG="v2.3.16")

    def test_explicit_version_must_match_tag(self):
        self.git("tag", "v2.3.16")
        self.invoke("dry-run", success=False, RELEASE_TAG="v2.3.16", RELEASE_VERSION="2.3.17")

    def test_invalid_versions_are_rejected_without_environment_injection(self):
        for version in ("", "3.0.0", "v2.3.16", "2.3.16-SNAPSHOT", "2.3.16\nINJECTED=true", "2.3.16;echo injected", "2.3.16/../../file"):
            with self.subTest(version=version):
                self.invoke("dry-run", success=False, RELEASE_VERSION=version)


if __name__ == "__main__":
    unittest.main()
