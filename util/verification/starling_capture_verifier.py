import argparse

from verification import verify


def parse_args():
    ap = argparse.ArgumentParser()
    ap.add_argument(
        '--information-filepath',
        required=True,
        help='The metadata of Starling Capture.'
    )
    ap.add_argument(
        '--signature-filepath',
        required=True,
        help='The digital signature of Starling Capture.'
    )
    return ap.parse_args()


if __name__ == '__main__':
    args = parse_args()
    verify(args.information_filepath, args.signature_filepath)
