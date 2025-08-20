package org.bread_experts_group

class MissingResourceError(path: String) : Error("Missing test resource [$path]")