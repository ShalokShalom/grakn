/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016-2018 Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/agpl.txt>.
 */

package ai.grakn.graql.internal.gremlin.fragment;

import ai.grakn.graql.Var;
import ai.grakn.graql.internal.gremlin.spanningtree.graph.DirectedEdge;
import ai.grakn.graql.internal.gremlin.spanningtree.graph.Node;
import ai.grakn.graql.internal.gremlin.spanningtree.graph.NodeId;
import ai.grakn.graql.internal.gremlin.spanningtree.util.Weighted;
import ai.grakn.kb.internal.EmbeddedGraknTx;
import ai.grakn.util.Schema;
import com.google.auto.value.AutoValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static ai.grakn.util.Schema.EdgeLabel.PLAYS;

@AutoValue
abstract class InPlaysFragment extends Fragment {

    @Override
    public abstract Var end();

    abstract boolean required();

    @Override
    public GraphTraversal<Vertex, ? extends Element> applyTraversalInner(
            GraphTraversal<Vertex, ? extends Element> traversal, EmbeddedGraknTx<?> graph, Collection<Var> vars) {
        GraphTraversal<Vertex, Vertex> vertexTraversal = Fragments.isVertex(traversal);
        if (required()) {
            vertexTraversal.inE(PLAYS.getLabel()).has(Schema.EdgeProperty.REQUIRED.name()).otherV();
        } else {
            vertexTraversal.in(PLAYS.getLabel());
        }

        return Fragments.inSubs(vertexTraversal);
    }

    @Override
    public String name() {
        if (required()) {
            return "<-[plays:required]-";
        } else {
            return "<-[plays]-";
        }
    }

    @Override
    public double internalFragmentCost() {
        return COST_TYPES_PER_ROLE;
    }

    @Override
    public Set<Weighted<DirectedEdge<Node>>> directedEdges(Map<NodeId, Node> nodes,
                                                           Map<Node, Map<Node, Fragment>> edges) {
        return directedEdges(NodeId.NodeType.PLAYS, nodes, edges);
    }
}
