package com.rethinkdb.ast.query.gen;

import com.rethinkdb.RethinkDBConnection;
import com.rethinkdb.ast.query.RqlQuery;
import com.rethinkdb.proto.Q2L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by ultrix on 03/07/15.
 */
public class Http extends RqlQuery {

    public Http(List<Object> args, java.util.Map<String, Object> optionalArgs) {
        this(null, args, optionalArgs);
    }

    public Http(RqlQuery prev, List<Object> args, Map<String, Object> optionalArgs) {
        super(prev, Q2L.Term.TermType.HTTP, args, optionalArgs);
    }

    @Override
    public List<Map<String, Object>> run(RethinkDBConnection connection) {

        Object object = super.run(connection);

        if (object instanceof Map) {
            ArrayList result = new ArrayList<Map<String, Object>>();
            result.add(object);
            return result;
        }

        return (List<Map<String, Object>>) super.run(connection);
    }
}
