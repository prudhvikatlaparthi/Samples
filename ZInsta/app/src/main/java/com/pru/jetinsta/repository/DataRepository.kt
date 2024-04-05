package com.pru.jetinsta.repository

import java.util.*

object DataRepository {
    val images =
        mutableListOf(
            Avatar(
                name = "Your Story",
                image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8ia_5XSezWCdZtW4MZWrGvDAoUnMfRGGpmg&usqp=CAU"
            ),
            Avatar(
                "Captain America",
                image = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_ogy4LXyt8uStelJZGENADYZlRpazfdO3SQ&usqp=CAU"
            ),
            Avatar(
                "Black Widow",
                image = "https://i.pinimg.com/474x/bd/e4/16/bde416607a7374b399125e3c05bccf18.jpg"
            ),
            Avatar(
                "Thor",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrT-v-SgxORPv5a33u9EPDsxc195t5W0YgFA&usqp=CAU"
            ),
            Avatar(
                "Thanos",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRi8EuLy02I_0cxbdrQx_VsGoJKE66QuYGWBw&usqp=CAU"
            ),
        )

    val feedData = mutableListOf(
        Feed(
            userAvatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRi8EuLy02I_0cxbdrQx_VsGoJKE66QuYGWBw&usqp=CAU",
            userName = "Thanos",
            photo = "https://www.awn.com/sites/default/files/styles/original/public/image/attached/1046351-aqc1260pubstillraw4kv6471014-lr.jpg?itok=TfmUJn4T",
            likesCount = 100,
            commentsCount = 10,
            date = getOldDate()
        ),
        Feed(
            userAvatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRi8EuLy02I_0cxbdrQx_VsGoJKE66QuYGWBw&usqp=CAU",
            userName = "Thanos",
            photo = "https://www.denofgeek.com/wp-content/uploads/2021/10/marvel_what_if_gamora.jpg?resize=768%2C432",
            likesCount = 100,
            commentsCount = 10,
            date = getOldDate(-1),
            hashTags = listOf("Daughter'slove", "Gamora")
        ),
        Feed(
            userAvatar = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ8ia_5XSezWCdZtW4MZWrGvDAoUnMfRGGpmg&usqp=CAU",
            userName = "Tony Stark",
            photo = "https://mickeyblog.com/wp-content/uploads/2020/04/TonyandMorganStark-1024x676.jpg",
            likesCount = 3333,
            commentsCount = 10000,
            date = getOldDate(-2),
            hashTags = listOf("loveyou3000", "MorganStark")
        ),
        Feed(
            userAvatar = "https://filmfare.wwmindia.com/thumb/content/2022/aug/she-hulk61660803294.jpg?width=1200&height=900",
            userName = "Hulk",
            photo = "https://cdn.vox-cdn.com/thumbor/AajsB8LAR1L7TLrV10glzfxeLaY=/0x0:1920x1080/1200x675/filters:focal(807x387:1113x693)/cdn.vox-cdn.com/uploads/chorus_image/image/70662506/thor_hulk_final.0.jpg",
            likesCount = 3333,
            commentsCount = 10000,
            date = getOldDate(-3),
            hashTags = listOf("friendsforever", "Thor")
        ),
        Feed(
            userAvatar = "https://filmfare.wwmindia.com/thumb/content/2022/aug/she-hulk61660803294.jpg?width=1200&height=900",
            userName = "Captain America",
            photo = "https://www.hollywoodreporter.com/wp-content/uploads/2018/12/captainamericafirstavenger_07-h_2018.jpg?w=1024",
            likesCount = 100000,
            commentsCount = 5000,
            date = getOldDate(-5),
            hashTags = listOf("Missyou:(")
        ),
        Feed(
            userAvatar = "https://people.com/thmb/GUTUXPpw_UJx3xSBKAhlUU-nLBg=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc():focal(734x169:736x171)/dr-strange-1-f3ebad95a41243809586ace4896ea7d8.jpg",
            userName = "Doctor Strange",
            photo = "https://bgr.com/wp-content/uploads/2021/12/spider-man-no-way-home-3.jpg?quality=82&strip=all",
            likesCount = 500000,
            commentsCount = 1000,
            date = getOldDate(-5),
            hashTags = listOf("PeterParker🤕"),
            description = "Tony left the trouble to me :😐"
        ),
    )
}

data class Avatar(
    var name: String,
    var image: String
)

data class Feed(
    var userAvatar: String,
    var userName: String,
    var photo: String,
    var likesCount: Int,
    var commentsCount: Int,
    var date: Date,
    var hashTags: List<String>? = null,
    var description: String? = null
)

fun getOldDate(daysBack: Int = 0): Date {
    val cal = Calendar.getInstance()
    if (daysBack != 0) {
        cal.add(Calendar.DATE, (daysBack * -1))
    }
    return cal.time
}