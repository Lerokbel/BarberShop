module DataLayer {
    requires Entities;
    requires java.sql;
    exports dbLayer.repositories;
    exports dbLayer.managers;
}