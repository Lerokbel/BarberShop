module TransportLayer {
    requires java.sql;
    requires Entities;
    requires DataLayer;

    exports serverEndPoint.threads;
    exports serverEndPoint;
}