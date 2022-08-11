#!/bin/bash

CAPTURE_DIR="$1"

INFORMATION_FILEPATH="${CAPTURE_DIR}/information.json"
SIGNATURE_FILEPATH="${CAPTURE_DIR}/signature.json"

poetry run python3 starling_capture_verifier.py \
    --information-filepath ${INFORMATION_FILEPATH} \
    --signature-filepath ${SIGNATURE_FILEPATH}
