package org.bread_experts_group.api.system.socket

/**
 * A socket operation timed out longer than the code was willing to wait.
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
class BSLSocketTimeoutExhausted : BSLSocketNotification("Timeout exhausted during socket communication")