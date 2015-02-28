package com.palmer.krawl.SpanningTree;

import com.palmer.krawl.delaunay_triangulation.PointDT;
import com.palmer.krawl.delaunay_triangulation.TriangleDT;

import java.util.*;

public class MinimumSpanningTree {

    private Set<PointDT> minimumTreePoints;
    private Set<PointDT> delaunayPoints;
    private Set<Edge> edges;

   public Set<PointDT> generateMiminumSpanningTree(List<TriangleDT> vertices) {
        minimumTreePoints = new HashSet<PointDT>();
        delaunayPoints = new HashSet<PointDT>();
        edges = new HashSet<Edge>();
        for (TriangleDT triangle: vertices) {
            delaunayPoints.add(triangle.p1());
            delaunayPoints.add(triangle.p2());
            delaunayPoints.add(triangle.p3());
            Edge edgeOne = new Edge(triangle.p1(), triangle.p2());
            edgeOne.setWeight((int) triangle.p1().distance(triangle.p2()));
            addEdgeToEdges(edgeOne);

            Edge edgeTwo = new Edge(triangle.p1(), triangle.p3());
            edgeTwo.setWeight((int) triangle.p1().distance(triangle.p3()));
            addEdgeToEdges(edgeTwo);

            Edge edgeThree = new Edge(triangle.p2(), triangle.p3());
            edgeThree.setWeight((int) triangle.p2().distance(triangle.p3()));
            addEdgeToEdges(edgeThree);
        }
       System.out.println("POINTS: "+ delaunayPoints.size());
       System.out.println("EDGES: " + edges.size());
       sortEdgesByWeight(edges);
//       defineTree(edges, delaunayPoints);
//       addLowestWeightedEdgeFromPoints(delaunayPoints);

       return minimumTreePoints;
   }

    private void addEdgeToEdges(Edge edge) {
        boolean addToEdges = true;
        for (Edge existingEdges: edges) {
            if (existingEdges.getP1().equals(edge.getP1()) && existingEdges.getP2().equals(edge.getP2()) ||
                    existingEdges.getP2().equals(edge.getP1()) && existingEdges.getP1().equals(edge.getP2())) {
                addToEdges = false;
                break;
            }
        }
        if (addToEdges) {
            edges.add(edge);
        }
    }

    private void sortEdgesByWeight(Set<Edge> edges){
        List<Object> sortEdges = Arrays.asList(edges.toArray());
        Collections.sort(sortEdges, new Comparator<Object> (){

            @Override
            public int compare(Object o1, Object o2) {
                Edge edge1 = (Edge)o1;
                Edge edge2 = (Edge)o2;
                return Integer.compare(edge1.getWeight(), edge2.getWeight());
            }
        });
    }

    private void defineTree(List<Edge> edges, List<PointDT> points) {
        List<PointDT> usedPoints = new ArrayList<PointDT>();
        for (PointDT point: points) {
            Edge currEdge = findLowestWeightEdge(point);
            if (currEdge == null) {
                //No edge found due to them being used
                break;
            } else {
                PointDT endEdgePoint = getEndEdgePoint(currEdge, point);
                if (endEdgePointIsConnectedToUnReachedEdge(point))

                currEdge.setReached(true);
//                usedPoints.add(point);
            }


        }
    }

    private Edge findLowestWeightEdge(PointDT point) {
        Edge returnEdge = null;
        int weight = Integer.MAX_VALUE;
        for (Edge edge: edges) {
            if (edge.getP1().equals(point) || edge.getP2().equals(point) && !edge.isReached()) {
                if (edge.getWeight() < weight) {
                    weight = edge.getWeight();
                    returnEdge = edge;
                }
            }
        }
        return returnEdge;
    }

    private PointDT getEndEdgePoint(Edge edge, PointDT point) {
        if (edge.getP1().equals(point)) {
            return edge.getP1();
        } else {
            return edge.getP2();
        }
    }

    private boolean endEdgePointIsConnectedToUnReachedEdge(PointDT point) {
            return false;
    }

    private void defineMinimumSpanningTreeFromEdges(List<Edge> edges) {
        List<Edge> reachedEdges = new ArrayList<Edge>();
        for (Edge edge : edges) {

        }
    }
    private void addLowestWeightedEdgeFromPoints(Set<PointDT> points){
        List<PointDT> reachedPoints = new ArrayList<PointDT>();
        List<Object> convertedSetOfPoints = Arrays.asList(points.toArray());
        for (Object point: convertedSetOfPoints) {
            PointDT currPoint = (PointDT)point;
            if (reachedPoints.size() == 0){
                reachedPoints.add(currPoint);
                minimumTreePoints.add(currPoint);
            } else {
                PointDT minimumEdgePoint = getMinimumPoint(convertedSetOfPoints, currPoint, reachedPoints);
                reachedPoints.add(minimumEdgePoint);
                minimumTreePoints.add(minimumEdgePoint);
            }
        }
    }

    private PointDT getMinimumPoint(List<Object> points, PointDT currentPoint, List<PointDT> reachedPoints) {
        int edgeWeight = Integer.MAX_VALUE;
        PointDT closestPoint = null;
        for (Object point : points) {
            PointDT thisPoint = (PointDT)point;
            if (!reachedPoints.contains(thisPoint) && !thisPoint.equals(currentPoint))
                if (currentPoint.distance(thisPoint) < edgeWeight) {
                    closestPoint = thisPoint;
                    edgeWeight = (int)currentPoint.distance(thisPoint);
                }
        }
        return closestPoint;
    }


}
