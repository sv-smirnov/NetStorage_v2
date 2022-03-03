module com.example.netstorage_v2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires io.netty.transport;
    requires io.netty.buffer;
    requires io.netty.codec;
    requires java.sql;

    opens com.example.netstorage_v2 to javafx.fxml;
    exports com.example.netstorage_v2;
}