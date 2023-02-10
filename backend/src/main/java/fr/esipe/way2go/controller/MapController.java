package fr.esipe.way2go.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/map")
public class MapController {
    record Point(double x, double y){ }
    @GetMapping
    //Page de la carte interactive
    public String map() {
        return "Page de la carte interactive";
    }


    static class DemoPayload{
        double startX;
        double startY;
        double endX;
        double endY;
        int distance;
        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public double getStartX() {
            return startX;
        }

        public void setStartX(double startX) {
            this.startX = startX;
        }

        public double getStartY() {
            return startY;
        }

        public void setStartY(double startY) {
            this.startY = startY;
        }

        public double getEndX() {
            return endX;
        }

        public void setEndX(double endX) {
            this.endX = endX;
        }

        public double getEndY() {
            return endY;
        }

        public void setEndY(double endY) {
            this.endY = endY;
        }

        @Override
        public String toString() {
            return "DemoPayload{" +
                    "startX=" + startX +
                    ", startY=" + startY +
                    ", endX=" + endX +
                    ", endY=" + endY +
                    ", distance=" + distance +
                    '}';
        }
    }
    @PostMapping("/newsimulation")
    //Nouvelle simulation
    public Point newSimulation(@RequestBody DemoPayload demo) {

        double x = 0;
        double y = 0;
        var coords=List.of(new Point(demo.startX,demo.startY),new Point(demo.endX,demo.endY));
        var size=coords.size();
        for (var point : coords) {
            x += point.x;
            y += point.y;
        }
        return new Point(x/size,y/size);
    }

}
