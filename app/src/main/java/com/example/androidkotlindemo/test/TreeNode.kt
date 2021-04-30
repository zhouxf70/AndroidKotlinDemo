package com.example.androidkotlindemo.test

/**
 * Created by zxf on 2021/4/26
 */
class TreeNode(val value: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
    override fun equals(other: Any?): Boolean {
        if (other !is TreeNode) return false
        return value == other.value
    }
}