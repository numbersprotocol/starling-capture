package io.numbersprotocol.starlingcapture.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImage
import io.numbersprotocol.starlingcapture.data.attached_image.AttachedImageDao
import io.numbersprotocol.starlingcapture.data.caption.Caption
import io.numbersprotocol.starlingcapture.data.caption.CaptionDao
import io.numbersprotocol.starlingcapture.data.information.Information
import io.numbersprotocol.starlingcapture.data.information.InformationDao
import io.numbersprotocol.starlingcapture.data.proof.Proof
import io.numbersprotocol.starlingcapture.data.proof.ProofDao
import io.numbersprotocol.starlingcapture.data.publish_history.PublishHistory
import io.numbersprotocol.starlingcapture.data.publish_history.PublishHistoryDao
import io.numbersprotocol.starlingcapture.data.signature.Signature
import io.numbersprotocol.starlingcapture.data.signature.SignatureDao
import io.numbersprotocol.starlingcapture.util.MimeType

@Database(
    entities = [
        Proof::class,
        Information::class,
        Signature::class,
        Caption::class,
        PublishHistory::class,
        AttachedImage::class
    ], version = 1
)
@TypeConverters(MimeType.RoomTypeConverter::class, Information.Importance.RoomTypeConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun proofDao(): ProofDao
    abstract fun informationDao(): InformationDao
    abstract fun signatureDao(): SignatureDao
    abstract fun captionDao(): CaptionDao
    abstract fun publishHistoryDao(): PublishHistoryDao
    abstract fun attachedImageDao(): AttachedImageDao
}