package com.example.androidkotlindemo.test

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by zxf on 2021/4/26
 */
fun main() {
    val root = TreeNode(1).apply {
        left = TreeNode(2).apply {
            left = TreeNode(4)
            right = TreeNode(5)
        }
        right = TreeNode(3).apply {
            left = TreeNode(7)
            right = TreeNode(8)
        }
    }
    println(traverse(root))
}

private fun traverse(root: TreeNode): ArrayList<Int?> {
    val result = ArrayList<Int?>()
    val linkedList = LinkedList<TreeNode>()
    linkedList.add(root)
    result.add(root.value)
    var isPos = false
    while (!linkedList.isEmpty()) {
        val size = linkedList.size
        val arr = Array<Int?>(size * 2) { null }
        for (i in 0 until size) {
            val pop = linkedList.pop()
            if (pop.left != null) linkedList.add(pop.left!!)
            if (pop.right != null) linkedList.add(pop.right!!)
            arr[i * 2] = pop.left?.value
            arr[i * 2 + 1] = pop.right?.value
        }
        for (i in arr.indices) {
            if (isPos) result.add(arr[i])
            else result.add(arr[size * 2 - i - 1])
        }
        isPos = !isPos
    }
    return result
}