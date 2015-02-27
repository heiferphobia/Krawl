package com.palmer.krawl.SpanningTree;

import com.palmer.krawl.delaunay_triangulation.PointDT;
import com.palmer.krawl.delaunay_triangulation.TriangleDT;

import java.util.*;

public class MinimumSpanningTree {

    private Set<PointDT> minimumTreePoints;
    private Set<PointDT> delaunayPoints;
    private List<Edge> edges;

   public Set<PointDT> generateMiminumSpanningTree(List<TriangleDT> vertices) {
        minimumTreePoints = new HashSet<PointDT>();
        delaunayPoints = new HashSet<PointDT>();
        edges = new ArrayList<Edge>();
        for (TriangleDT triangle: vertices) {
            delaunayPoints.add(triangle.p1());
            delaunayPoints.add(triangle.p2());
            delaunayPoints.add(triangle.p3());
            Edge edgeOne = new Edge(triangle.p1(), triangle.p2());
            edgeOne.setWeight((int) triangle.p1().distance(triangle.p2()));
            edges.add(edgeOne);

            Edge edgeTwo = new Edge(triangle.p1(), triangle.p3());
            edgeTwo.setWeight((int) triangle.p1().distance(triangle.p3()));
            edges.add(edgeTwo);

            Edge edgeThree = new Edge(triangle.p2(), triangle.p3());
            edgeThree.setWeight((int) triangle.p2().distance(triangle.p3()));
            edges.add(edgeThree);
        }
       sortEdgesByWeight(edges);
       addLowestWeightedEdgeFromPoints(delaunayPoints);

       return minimumTreePoints;
   }

    private void sortEdgesByWeight(List<Edge> edges){
        Collections.sort(edges, new Comparator<Edge> (){

            @Override
            public int compare(Edge o1, Edge o2) {
                return Integer.compare(o1.getWeight(), o2.getWeight());
            }
        });
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
