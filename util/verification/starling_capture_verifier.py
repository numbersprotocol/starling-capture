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
    ap.add_argument(
        '--signer-wallet',
        help="Signer's Ethereum wallet address."
    )
    return ap.parse_args()


if __name__ == '__main__':
    args = parse_args()
    #print(args.information_filepath)
    #print(args.signature_filepath)
    #print(args.signer_wallet)
    verify(args.information_filepath, args.signature_filepath, args.signer_wallet)
