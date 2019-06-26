package graphInformation;

import com.sun.istack.internal.NotNull;

import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.*;

public class GraphInformation {
    private static final float DIFF_SCALE = 1000.f;

    private GraphInformation() {}

    /**
     * @return the distance between the GraphNode a and the GraphNode b
     */
    public static float getDistanceBetween(@NotNull GraphNode a, @NotNull GraphNode b) {
        float xDiff = a.x - b.x;
        float yDiff = a.y - b.y;
        float zDiff = a.z - b.z;
        return (float) Math.sqrt(xDiff*xDiff + yDiff*yDiff + zDiff*zDiff);
    }

    /**
     * Returns the position of the given GraphNode as Vector3D.
     * @param graphNode The GraphNode of which the position is returned
     * @return The position of the given GraphNode as Vector3D
     */
    public static Vector3D getPositionOf(@NotNull GraphNode graphNode) {
        return new Vector3D(graphNode.x, graphNode.y, graphNode.z);
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

        if (selfDistanceSquared < neighborDistanceSquared)
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
            if (closestNeighbor == null)
                break;
            else
                graphNode = closestNeighbor;
        }
        return graphNode;
    }

    /**
     * Returns the GraphNode that is closest to the given startNode that is owned by the given player
     * @return The GraphNode, that is owned by playerNumber and was found closest to the given startNode. If no node
     * could be found, null is returned.
     */
    public static GraphNode getClosestGraphNodeOfPlayer(@NotNull GraphNode startNode, int playerNumber) {
        ArrayDeque<GraphNode> graphNodesToVisit = new ArrayDeque<>();
        HashSet<GraphNode> alreadyToVisit = new HashSet<>();

        graphNodesToVisit.add(startNode);
        alreadyToVisit.add(startNode);

        while (!graphNodesToVisit.isEmpty()) {
            GraphNode node = graphNodesToVisit.poll();

            if (node.owner == playerNumber)
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
     * Returns an integer that can be used to compare the two given GraphNodes a and b in respect to how close they are
     * to the given target point. If a is closer to targetPoint, the result will be negative. If b is closer to the
     * target point, the result will be positiv. If a and b are equally close to the target point 0 is returned.
     * @param targetNodePosition The position to compare the distances of a and b to
     */
    public static int graphNodeComparison(Vector3D targetNodePosition, GraphNode a, GraphNode b) {
        Vector3D aPos = getPositionOf(a);
        Vector3D bPos = getPositionOf(b);

        float aDiff = Vector3D.getDistanceSquaredBetween(aPos, targetNodePosition);
        float bDiff = Vector3D.getDistanceSquaredBetween(bPos, targetNodePosition);

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
}
