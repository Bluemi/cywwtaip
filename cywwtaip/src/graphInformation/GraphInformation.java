package graphInformation;

import com.sun.istack.internal.NotNull;

import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.*;
import java.util.function.Predicate;

public class GraphInformation {
    private static final float DIFF_SCALE = 1000.f;
    public static final float AVERAGE_NEIGHBOR_DISTANCE = 0.01978f;
    public static final float AVERAGE_NEIGHBOR_DISTANCE_SQUARED = AVERAGE_NEIGHBOR_DISTANCE * AVERAGE_NEIGHBOR_DISTANCE;
    public static final float MAX_NEIGHBOR_DISTANCE = 0.0823f;
    public static final float MAX_NEIGHBOR_DISTANCE_SQUARED = MAX_NEIGHBOR_DISTANCE * MAX_NEIGHBOR_DISTANCE;
    public static final float MIN_NEIGHBOR_DISTANCE = 0.0163f;
    public static final float MIN_NEIGHBOR_DISTANCE_SQUARED = MIN_NEIGHBOR_DISTANCE * MIN_NEIGHBOR_DISTANCE;

    private GraphInformation() {}

    /**
     * @return the distance between the GraphNode a and the GraphNode b
     */
    public static float getDistanceBetween(@NotNull GraphNode a, @NotNull GraphNode b) {
        return Vector3D.getDistanceBetween(getPositionOf(a), getPositionOf(b));
    }

    /**
     * @return the squared distance between the GraphNode a and the GraphNode b
     */
    public static float getDistanceSquaredBetween(@NotNull GraphNode a, @NotNull GraphNode b) {
        return Vector3D.getDistanceSquaredBetween(getPositionOf(a), getPositionOf(b));
    }

    /**
     * Returns the position of the given GraphNode as Vector3D.
     * @param graphNode The GraphNode of which the position is returned
     * @return The position of the given GraphNode as Vector3D
     */
    public static Vector3D getPositionOf(@NotNull GraphNode graphNode) {
        return new Vector3D(graphNode.x, graphNode.y, graphNode.z).normalized();
    }

    /**
     * Returns the neighbor of the given graphNode, that is closest to the given position.
     * If graphNode itself is closer than the other neighbors, null is returned.
     * @param graphNode The GraphNode whose neighbors are searched.
     * @param position The position to which the closest neighbor is searched
     * @return The neighbor of graphNode, which is the closest to the given position
     */
    public static GraphNode getClosestNeighborTo(@NotNull GraphNode graphNode, @NotNull Vector3D position) {
        GraphNode closestNeighbor = getClosestOf(graphNode.neighbors, position);

        float neighborDistanceSquared = Vector3D.getDistanceSquaredBetween(getPositionOf(closestNeighbor), position);
        float selfDistanceSquared = Vector3D.getDistanceSquaredBetween(getPositionOf(graphNode), position);

        if (selfDistanceSquared <= neighborDistanceSquared)
            return null;

        return closestNeighbor;
    }

    /**
     * Returns the GraphNode of the given graphNodes, that is closest to the given position.
     * @param graphNodes The GraphNodes from which to find the closest to position. This array should not be null and
     *                   not be empty.
     * @param position The position to which the closest graphNode is searched. Should not be null.
     * @return The graphNode, which is the closest to the given position
     */
    public static GraphNode getClosestOf(@NotNull GraphNode[] graphNodes, @NotNull Vector3D position) {
        GraphNode closestGraphNode = graphNodes[0];
        float closestSquaredDistance = Vector3D.getDistanceSquaredBetween(getPositionOf(closestGraphNode), position);

        // i = 1, because first graphNode can be skipped, because it is assumed at start
        for (int i = 1; i < graphNodes.length; i++) {
            float squaredDistance = Vector3D.getDistanceSquaredBetween(getPositionOf(graphNodes[i]), position);
            if (squaredDistance < closestSquaredDistance) {
                closestGraphNode = graphNodes[i];
                closestSquaredDistance = squaredDistance;
            }
        }

        return closestGraphNode;
    }

    /**
     * Returns the graph node, that is closest to the given position in the whole Graph, starting by the given startNode
     * @param startNode The graphNode from where to start searching
     * @param position The position, for which the closest GraphNode should be found
     * @return The GraphNode in the Graph of startNode, that is closest to the given position
     */
    public static GraphNode getClosestGraphNodeTo(@NotNull GraphNode startNode, @NotNull Vector3D position) {
        GraphNode graphNode = startNode;

        while (true) {
            GraphNode closestNeighbor = getClosestNeighborTo(graphNode, position);
            if (closestNeighbor == null) {
                assert Vector3D.getDistanceBetween(getPositionOf(graphNode), position) < MAX_NEIGHBOR_DISTANCE * 2.f;
                break;
            } else {
                graphNode = closestNeighbor;
            }
        }
        return graphNode;
    }

    /**
     * Returns the GraphNode that is closest to the given startNode that matches the given predicate.
     * @return The GraphNode, that matches the given predicate and was found closest to the given startNode. If no node
     * could be found, null is returned.
     */
    public static GraphNode getClosestGraphNodeWithPredicate(
            @NotNull GraphNode startNode,
            @NotNull Predicate<GraphNode> predicate
    ) {
        PriorityQueue<GraphNode> graphNodesToVisit = new PriorityQueue<>(
                new GraphNodePositionComparator(getPositionOf(startNode))
        );
        HashSet<GraphNode> alreadyToVisit = new HashSet<>();

        graphNodesToVisit.add(startNode);
        alreadyToVisit.add(startNode);

        while (!graphNodesToVisit.isEmpty()) {
            GraphNode node = graphNodesToVisit.poll();

            if (predicate.test(node))
                return node;

            for (GraphNode neighbor : node.neighbors) {
                if (!alreadyToVisit.contains(neighbor)) {
                    graphNodesToVisit.add(neighbor);
                    alreadyToVisit.add(neighbor);
                }
            }
        }

        return null;
    }

    /**
     * Returns the n GraphNodes which are closest to the given position that matches the given predicate.
     * It is assumed that the given startNode is the closest node of the graph to the given position.
     * @return The List of GraphNodes, that matches the given predicate and was found closest to the given startNode.
     * If no node could be found, null is returned an empty list is returned.
     */
    public static ArrayList<GraphNode> getClosestGraphNodesWithPredicate(
            @NotNull GraphNode startNode,
            @NotNull Vector3D position,
            @NotNull Predicate<GraphNode> predicate,
            int n
    ) {
        PriorityQueue<GraphNode> graphNodesToVisit = new PriorityQueue<>(
                new GraphNodePositionComparator(position)
        );
        HashSet<GraphNode> alreadyToVisit = new HashSet<>();
        ArrayList<GraphNode> foundNodes = new ArrayList<>();

        graphNodesToVisit.add(startNode);
        alreadyToVisit.add(startNode);

        while (!graphNodesToVisit.isEmpty()) {
            GraphNode node = graphNodesToVisit.poll();

            if (predicate.test(node)) {
                foundNodes.add(node);
                if (foundNodes.size() == n) {
                    return foundNodes;
                }
            }

            for (GraphNode neighbor : node.neighbors) {
                if (!alreadyToVisit.contains(neighbor)) {
                    graphNodesToVisit.add(neighbor);
                    alreadyToVisit.add(neighbor);
                }
            }
        }

        return foundNodes;
    }

    /**
     * Returns an integer that can be used to compare the two given GraphNodes a and b in respect to how close they are
     * to the given target point. If a is closer to targetPoint, the result will be negative. If b is closer to the
     * target point, the result will be positiv. If a and b are equally close to the target point 0 is returned.
     * @param targetNodePosition The position to compare the distances of a and b to
     */
    public static int graphNodeComparison(Vector3D targetNodePosition, GraphNode a, GraphNode b) {
        Vector3D aPos = getPositionOf(a);
        Vector3D bPos = getPositionOf(b);

        float aDiff = Vector3D.getDistanceBetween(aPos, targetNodePosition);
        float bDiff = Vector3D.getDistanceBetween(bPos, targetNodePosition);

        float diff = aDiff - bDiff;
        int intDiff = Math.round(diff * DIFF_SCALE);

        if (intDiff == 0)
            intDiff = (int)Math.signum(diff);

        return intDiff;
    }

    private static class GraphNodeWrapper {
        public GraphNode graphNode;
        public GraphNodeWrapper predecessor;
        public float rating; // This is the number of GraphNodes to come to this GraphNode

        public GraphNodeWrapper(GraphNode graphNode, GraphNodeWrapper predecessor, float rating) {
            this.graphNode = graphNode;
            this.predecessor = predecessor;
            this.rating = rating;
        }

        public GraphNodeWrapper(GraphNode graphNode) {
            this.graphNode = graphNode;
            this.rating = 0;
            this.predecessor = null;
        }

        @Override
        public int hashCode() {
            return this.graphNode.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof GraphNodeWrapper) {
                GraphNodeWrapper otherGraphNodeWrapper = (GraphNodeWrapper) other;
                return this.graphNode.equals(otherGraphNodeWrapper.graphNode);
            }
            return false;
        }

        public ArrayList<GraphNode> toList() {
            ArrayList<GraphNode> nodes = new ArrayList<>();

            GraphNodeWrapper currentWrapper = this;
            while (currentWrapper != null) {
                nodes.add(currentWrapper.graphNode);
                currentWrapper = currentWrapper.predecessor;
            }

            Collections.reverse(nodes);

            return nodes;
        }
    }

    private static class GraphNodeWrapperComparator implements Comparator<GraphNodeWrapper> {
        private Vector3D targetNodePosition;

        public GraphNodeWrapperComparator(GraphNode targetNode) {
            this.targetNodePosition = getPositionOf(targetNode);
        }

        @Override
        public int compare(GraphNodeWrapper a, GraphNodeWrapper b) {
            return graphNodeComparison(this.targetNodePosition, a.graphNode, b.graphNode);
        }
    }

    /**
     * Implements the A* algorithm to find the optimal path from startNode to targetNode.
     * @param startNode The GraphNode to start from
     * @param targetNode The GraphNode to find a path to
     * @return A list of GraphNodes, that is the optimal path from startNode to targetNode. If no path could be found
     * null is returned.
     */
    public static ArrayList<GraphNode> getPathTo(GraphNode startNode, GraphNode targetNode) {
        PriorityQueue<GraphNodeWrapper> openList = new PriorityQueue<>(new GraphNodeWrapperComparator(targetNode));
        HashSet<GraphNode> closedSet = new HashSet<>();
        HashMap<GraphNode, Float> distanceToStart = new HashMap<>();
        distanceToStart.put(startNode, 0.f);

        GraphNodeWrapper startNodeWrapper = new GraphNodeWrapper(startNode);
        openList.add(startNodeWrapper);

        do {
            GraphNodeWrapper currentNode = openList.poll();
            assert currentNode != null;

            if (currentNode.graphNode.equals(targetNode)) {
                return currentNode.toList();
            }

            closedSet.add(currentNode.graphNode);

            expandNode(currentNode, openList, closedSet, distanceToStart, targetNode);
        } while (!openList.isEmpty());

        return null;
    }

    private static void expandNode(
            GraphNodeWrapper currentNode,
            PriorityQueue<GraphNodeWrapper> openList,
            HashSet<GraphNode> closedSet,
            HashMap<GraphNode, Float> distanceToStart,
            GraphNode targetNode
    ) {
        float distanceToCurrentNode = distanceToStart.get(currentNode.graphNode);

        for (GraphNode neighbor : currentNode.graphNode.neighbors) {
            if (neighbor.blocked)
                continue;
            if (closedSet.contains(neighbor))
                continue;

            float tentativeDistanceOfNeighbor = distanceToCurrentNode + getDistanceBetween(currentNode.graphNode, neighbor);
            GraphNodeWrapper neighborWrapper = new GraphNodeWrapper(neighbor, currentNode, 0.f);

            boolean neighborInOpenList = openList.contains(neighborWrapper);
            if (neighborInOpenList && tentativeDistanceOfNeighbor >= distanceToStart.get(neighbor))
                continue;

            distanceToStart.put(neighbor, tentativeDistanceOfNeighbor);

            neighborWrapper.rating = tentativeDistanceOfNeighbor + getDistanceBetween(neighbor, targetNode);

            if (neighborInOpenList) {
                openList.remove(neighborWrapper);
            }
            openList.add(neighborWrapper);
        }

    }

    /**
     * Chooses a random node of the given nodes and returns it.
     * @param graph The graph to choose a random node from
     * @return The randomly chosen GraphNode
     */
    public static GraphNode getRandomNode(GraphNode[] graph) {
        int index =  new Random(System.currentTimeMillis()).nextInt(graph.length);
        return graph[index];
    }

    /**
     * Returns a randomly selected node, that is accessible by the neighbors of the given graphNode.
     * @param graphNode The graphNode to start from
     * @return A randomly selected GraphNode
     */
    public static GraphNode getRandomNode(GraphNode graphNode) {
        Vector3D position = Vector3D.getRandomNormalized();
        return GraphInformation.getClosestGraphNodeTo(graphNode, position);
    }

    public static class GraphNodePositionComparator implements Comparator<GraphNode> {
        private Vector3D position;

        public GraphNodePositionComparator(Vector3D position) {
            this.position = position;
        }

        @Override
        public int compare(GraphNode g1, GraphNode g2) {
            return graphNodeComparison(position, g1, g2);
        }
    }

    /**
     * overwrites every neighbor of a blocked node to be blocked as well
     * @param graph The array of GraphNodes to prepare
     */
    public static void prepareGraph(GraphNode[] graph) {
        ArrayList<GraphNode> blockedNodes = new ArrayList<>();

        for (GraphNode g : graph)
            if (g.blocked)
                blockedNodes.add(g);

        for (GraphNode g : blockedNodes)
            for (GraphNode n : g.neighbors)
                n.blocked = true;
    }

    /**
     * Determines a path from the given start node. The start node is the first element of the path.
     * The path only contains GraphNodes, that match the given Predicate. To determine the next node in the path, the
     * neighbor of last node, which matches the predicate and is as far as possible away from the start node is added to
     * the path.
     * @param startNode The node to start with
     * @param predicate The predicate all members, except the start node, should match
     * @param maxLength The maximal number of nodes in this path
     * @return A path, with elements matching the given predicate
     */
    public static ArrayList<GraphNode> getPathWithPredicate(GraphNode startNode, Predicate<GraphNode> predicate, int maxLength) {
        ArrayList<GraphNode> path = new ArrayList<>();
        Comparator<GraphNode> nodeComparator = new GraphNodePositionComparator(getPositionOf(startNode));
        HashSet<GraphNode> alreadyFound = new HashSet<>();

        Predicate<GraphNode> predicateAndNotAlreadyFound = predicate.and(graphNode -> !alreadyFound.contains(graphNode));

        GraphNode currentNode = startNode;

        for (int i = 0; i < maxLength; i++) {
            path.add(currentNode);
            alreadyFound.add(currentNode);

            GraphNode maxNode = getMaxWithPredicate(
                    currentNode.neighbors,
                    nodeComparator,
                    predicateAndNotAlreadyFound
            );

            if (maxNode == null) {
                break;
            }
            currentNode = maxNode;
        }

        return path;
    }

    /**
     * Searches the maximum specified by the ordering of the given comparator, that matches the given predicate.
     * If nodes is empty or non of the given nodes match the given predicate null is returned.
     * @param nodes The array of nodes to search the maximum in
     * @param comparator The comparator defining which node is maximal
     * @param predicate A predicate defining, which nodes are used for the consideration
     * @return The node with the highest value, given by comparator, that matches the given predicate.
     */
    public static GraphNode getMaxWithPredicate(GraphNode[] nodes, Comparator<GraphNode> comparator, Predicate<GraphNode> predicate) {
        GraphNode maxNode = null;
        for (GraphNode g : nodes) {
            if (predicate.test(g)) {
                if (maxNode == null) {
                    maxNode = g;
                } else {
                    if (comparator.compare(maxNode, g) < 0) {
                        maxNode = g;
                    }
                }
            }
        }

        return maxNode;
    }
}
