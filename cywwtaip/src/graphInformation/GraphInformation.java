package graphInformation;

import com.sun.istack.internal.NotNull;

import lenz.htw.cywwtaip.world.GraphNode;
import math.Vector3D;

import java.util.ArrayDeque;
import java.util.HashSet;

public class GraphInformation {
    private GraphInformation() {}

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
}
