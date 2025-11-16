package org.bread_experts_group.api.feature

class NoFeatureAvailableException(featureName: String) : Exception("No found feature for \"$featureName\"")