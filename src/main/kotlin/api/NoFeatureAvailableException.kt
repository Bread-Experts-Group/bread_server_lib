package org.bread_experts_group.api

class NoFeatureAvailableException(featureName: String) : Exception("No found feature for \"$featureName\"")