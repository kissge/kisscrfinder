#!/usr/bin/env bash
# Creates the release signing key (once) and registers it as GitHub Actions secrets.
# Passwords are generated randomly and never printed.
#
#   scripts/setup-signing.sh               # create key if missing, then set GitHub secrets (needs `gh auth login`)
#   scripts/setup-signing.sh --local-only  # create key and keystore.properties only
#
# BACK UP the keystore: if it is lost, installed apps can no longer be updated (uninstall required).
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
KEYSTORE=${KEYSTORE:-$HOME/.android/kisscrfinder-release.jks}
PROPS=${KEYSTORE_PROPERTIES:-$ROOT/keystore.properties}
ALIAS=kisscrfinder

KEYTOOL=keytool
[[ -n ${JAVA_HOME:-} ]] && KEYTOOL=$JAVA_HOME/bin/keytool

if [[ ! -f $KEYSTORE ]]; then
  mkdir -p "$(dirname "$KEYSTORE")"
  SIGNING_PASSWORD=$(openssl rand -base64 32 | tr -d '/+=')
  export SIGNING_PASSWORD
  "$KEYTOOL" -genkeypair -keystore "$KEYSTORE" -storetype PKCS12 -alias "$ALIAS" \
    -keyalg RSA -keysize 4096 -validity 10000 -dname "CN=kisscrfinder" \
    -storepass:env SIGNING_PASSWORD -keypass:env SIGNING_PASSWORD
  chmod 600 "$KEYSTORE"
  (
    umask 077
    printf 'storeFile=%s\nstorePassword=%s\nkeyAlias=%s\nkeyPassword=%s\n' \
      "$KEYSTORE" "$SIGNING_PASSWORD" "$ALIAS" "$SIGNING_PASSWORD" > "$PROPS"
  )
  echo "Created $KEYSTORE and $PROPS"
elif [[ ! -f $PROPS ]]; then
  echo "error: $KEYSTORE exists but $PROPS is missing; restore it from your backup." >&2
  exit 1
fi

SIGNING_PASSWORD=$(sed -n 's/^storePassword=//p' "$PROPS")

if [[ ${1:-} == --local-only ]]; then
  exit 0
fi

base64 < "$KEYSTORE" | tr -d '\n' | gh secret set SIGNING_KEYSTORE_BASE64
printf '%s' "$SIGNING_PASSWORD" | gh secret set SIGNING_PASSWORD
echo "Registered SIGNING_KEYSTORE_BASE64 and SIGNING_PASSWORD as GitHub Actions secrets."
