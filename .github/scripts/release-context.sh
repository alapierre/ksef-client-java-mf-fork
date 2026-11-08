#!/usr/bin/env bash
set -euo pipefail

fail() {
  echo "::error::$*" >&2
  exit 1
}

mode=${1:?Expected dry-run or publish}
tag=${RELEASE_TAG:-}
version=${RELEASE_VERSION:-}
version_pattern='^2\.[0-9]+\.[0-9]+(-[A-Za-z0-9]+([.-][A-Za-z0-9]+)*)?$'

[[ "$mode" == dry-run || "$mode" == publish ]] || fail 'Unknown release mode'
if [[ -n "$tag" ]]; then
  [[ "$tag" == v* ]] || fail 'The release tag must start with v'
  [[ -z "$version" || "$version" == "${tag#v}" ]] || fail 'Version must match the tag'
  version=${tag#v}
fi
[[ "$version" =~ $version_pattern && "$version" != *SNAPSHOT* ]] ||
  fail 'Expected a version such as 2.3.16 or 2.4.0-rc.1 (without v or SNAPSHOT)'

commit=$(git rev-parse HEAD)
if [[ -n "$tag" ]]; then
  tagged_commit=$(git rev-parse --verify "refs/tags/$tag^{commit}") || fail 'Tag does not exist'
  [[ "$commit" == "$tagged_commit" ]] || fail 'Checkout does not match the requested tag'
fi

if [[ "$mode" == publish ]]; then
  [[ -n "$tag" ]] || fail 'Publication requires an existing release tag'
  case "${GITHUB_EVENT_NAME:-}" in
    push)
      [[ "${GITHUB_REF:-}" == "refs/tags/$tag" ]] || fail 'Push must be for the requested tag'
      ;;
    workflow_dispatch)
      [[ "${GITHUB_REF:-}" == refs/heads/main ]] || fail 'Start manual publication from the main workflow'
      ;;
    *) fail 'Unsupported publication event' ;;
  esac
  git merge-base --is-ancestor "$commit" refs/remotes/origin/main ||
    fail 'Release commit must belong to origin/main'
fi

: "${GITHUB_ENV:?GITHUB_ENV must point to the Actions environment file}"
{
  echo "VERSION=$version"
  echo "BUILD_TIMESTAMP=$(git show -s --format=%cI HEAD)"
} >> "$GITHUB_ENV"
echo "Validated $mode for version $version at $commit"
