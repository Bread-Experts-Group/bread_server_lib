module bread.server.lib.main {
    requires java.base;
    requires java.logging;
    requires java.xml;
    requires java.desktop;

    requires kotlin.stdlib;
    requires kotlin.reflect;

    exports org.bread_experts_group.model.natives;
}