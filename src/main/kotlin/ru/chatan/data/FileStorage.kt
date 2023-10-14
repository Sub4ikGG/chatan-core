package ru.chatan.data

import io.minio.MinioClient
import io.minio.RemoveObjectArgs
import io.minio.UploadObjectArgs
import java.io.File

class FileStorage {

    private val minio: MinioClient = MinioClient.builder()
        .endpoint(S3_ENDPOINT)
        .credentials(S3_ACCESS_KEY, S3_SECRET_KEY)
        .build()

    fun load(file: File) {
        minio.uploadObject(
            UploadObjectArgs.builder()
                .bucket(S3_BUCKET)
                .`object`(file.name)
                .filename(file.path)
                .build()
        )
    }

    fun buildHref(uuid: String, type: String): String =
        "https://s3.timeweb.com/$S3_BUCKET/$uuid.$type"

//    fun buildNoImageHref() = "https://s3.timeweb.com/$S3_BUCKET/no-image.jpeg"

    fun remove(uuid: String, type: String) {
        minio.removeObject(
            RemoveObjectArgs.builder()
                .bucket(S3_BUCKET)
                .`object`("$uuid.$type")
                .build()
        )
    }

    companion object {
        private const val S3_ENDPOINT = "https://s3.timeweb.com"
        private const val S3_ACCESS_KEY = "cj47329"
        private const val S3_SECRET_KEY = "4229006efe853d20b75045e82e3dc65c"
        private const val S3_BUCKET = "2ce12850-db20e46b-ab89-4c4a-8ce9-6b104cf00313/avatar"
    }

}