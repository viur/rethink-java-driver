package com.rethinkdb;

import com.rethinkdb.model.DBObject;
import com.rethinkdb.proto.*;
import com.rethinkdb.response.DBResultFactory;

public class RethinkDBConnection {

    private String hostname;
    private String authKey;
    private int port;
    private int timeout;
    private String dbName = null;

    private SocketChannelFacade socket = new SocketChannelFacade();
    private Q2L.Query dbOption;

    public RethinkDBConnection() {
        this(RethinkDBConstants.DEFAULT_HOSTNAME);
    }

    public RethinkDBConnection(String hostname) {
        this(hostname, RethinkDBConstants.DEFAULT_PORT);
    }

    public RethinkDBConnection(String hostname, int port) {
        this(hostname, port, "");
    }

    public RethinkDBConnection(String hostname, int port, String authKey) {
        this(hostname, port, authKey, RethinkDBConstants.DEFAULT_TIMEOUT);
    }

    public RethinkDBConnection(String hostname, int port, String authKey, int timeout) {
        this.hostname = hostname;
        this.port = port;
        this.authKey = authKey;
        this.timeout = timeout;
        reconnect();
    }

    // TODO: use timeout
    public void reconnect() {
        socket.connect(hostname, port);
        socket.writeLEInt(Q2L.VersionDummy.Version.V0_2.getNumber());
        socket.writeStringWithLength(authKey);

        String result = socket.readString();
        if (!result.startsWith("SUCCESS")) {
            throw new RethinkDBException(result);
        }
    }

    // TODO: When partial implemented add option to wait for jobs to finish
    public void close() {
        socket.close();
    }

    public void use(String dbName) {
        this.dbName = dbName;
    }

    // TODO: this needs to be protected
    public DBObject run(Q2L.Query.Builder query) {
        setDbOptionIfNeeded(query, this.dbName);
        socket.write(query.build().toByteArray());
        Q2L.Response response = socket.read();
        return DBResultFactory.convert(response);
    }

    public void setDbOptionIfNeeded(Q2L.Query.Builder q, String db) {
        if (!ProtoUtil.hasKey(q.getGlobalOptargsList(), "db") && db != null) {
            q.addGlobalOptargs(
                    RAssocPairBuilder.queryPair(
                            "db",
                            Q2L.Term.newBuilder()
                                .setType(Q2L.Term.TermType.DB)
                                .addArgs(RTermBuilder.datumTerm(db)).build()
                            )
            );
        }
    }

}