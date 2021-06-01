package com.marianpusk.knihy

import java.text.Normalizer

object StringUtils {

    fun unaccent(src: String?): String {
        return Normalizer
            .normalize(src, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
    }

}