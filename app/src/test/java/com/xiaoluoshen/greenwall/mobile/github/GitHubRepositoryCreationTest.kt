package com.xiaoluoshen.greenwall.mobile.github

import java.net.HttpURLConnection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GitHubRepositoryCreationTest {
    @Test
    fun `repository name validator accepts valid name`() {
        assertNull(RepositoryNameValidator.validate("greenwall-20260818"))
    }

    @Test
    fun `repository name validator rejects unsafe name`() {
        assertEquals("仓库名称不能包含空格、斜杠或控制字符", RepositoryNameValidator.validate("greenwall repo"))
        assertEquals("仓库名称不能以空格开头或结尾", RepositoryNameValidator.validate(" greenwall"))
        assertEquals("仓库名称不可用", RepositoryNameValidator.validate(".."))
    }

    @Test
    fun `repository creation error maps missing permissions`() {
        assertEquals(
            "令牌没有创建仓库的权限。经典令牌需要 repo 权限；细粒度令牌需要 Administration 写入权限",
            repositoryCreationFailureMessage(HttpURLConnection.HTTP_FORBIDDEN, "Resource not accessible by personal access token"),
        )
    }

    @Test
    fun `repository creation error maps duplicate repository name`() {
        assertEquals(
            "该仓库名称已存在，请使用其他名称",
            repositoryCreationFailureMessage(422, "name already exists on this account"),
        )
    }

    @Test
    fun `repository creation error maps expired token`() {
        assertEquals(
            "GitHub 认证已失效，请退出后使用新的个人访问令牌重新登录",
            repositoryCreationFailureMessage(HttpURLConnection.HTTP_UNAUTHORIZED, "Bad credentials"),
        )
    }
}
