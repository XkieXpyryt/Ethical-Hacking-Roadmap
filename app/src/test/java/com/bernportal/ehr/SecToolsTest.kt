package com.bernportal.ehr

import com.bernportal.ehr.util.SecTools
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecToolsTest {
    @Test fun cvssCriticalVector() {
        val r = SecTools.cvss("N", "L", "N", "N", "U", "H", "H", "H")
        assertEquals(9.8, r.score, 0.001); assertEquals("Critical", r.severity)
    }
    @Test fun cvssScopeChanged() {
        val r = SecTools.cvss("N", "L", "N", "N", "C", "H", "H", "H")
        assertEquals(10.0, r.score, 0.001)
    }
    @Test fun cvssMedium() {
        val r = SecTools.cvss("N", "L", "N", "R", "U", "L", "L", "N")
        assertEquals(5.4, r.score, 0.001); assertEquals("Medium", r.severity)
    }
    @Test fun cvssNoImpactIsZero() { assertEquals(0.0, SecTools.cvss("N", "L", "N", "N", "U", "N", "N", "N").score, 0.001) }
    @Test fun subnet27() {
        val s = SecTools.subnet("10.10.10.77/27")!!
        assertEquals("10.10.10.64", s.network); assertEquals("10.10.10.95", s.broadcast); assertEquals(30, s.usable); assertTrue(s.isPrivate)
    }
    @Test fun subnetMaskNotation() {
        val s = SecTools.subnet("172.16.4.9/255.255.252.0")!!
        assertEquals(22, s.prefix); assertEquals("172.16.4.0", s.network); assertEquals("172.16.7.255", s.broadcast)
    }
    @Test fun subnetPublic() { assertTrue(!SecTools.subnet("8.8.8.8/32")!!.isPrivate) }
    @Test fun subnetInvalid() { assertNull(SecTools.subnet("300.1.1.1/24")); assertNull(SecTools.subnet("10.0.0.1/40")) }
    @Test fun convert() { val c = SecTools.convert("FF", 16); assertNotNull(c); assertEquals("255", c!!.first); assertEquals("11111111", c.second) }
    @Test fun sha256() { assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", SecTools.hash("SHA-256", "hello")) }
    @Test fun weakPassword() { assertTrue(SecTools.strength("password2024").bits < 40) }
}
