package org.reactome.web.diagram.data.layout;

import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;

import java.util.function.Function;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface NodeProperties {

    Double getX();

    Double getY();

    Double getWidth();

    Double getHeight();

    public final static class Builder implements NodeProperties {
        private Double x = 0d;
        private Double y = 0d;
        private Double width = 0d;
        private Double height = 0d;

        public Builder copy(NodeProperties properties) {
            this.x = properties.getX();
            this.y = properties.getY();
            this.width = properties.getWidth();
            this.height = properties.getHeight();
            return this;
        }

        public Builder padding(Double padding) {
            this.x = x + padding;
            this.y = y + padding;
            this.width = width - 2 * padding;
            this.height = height - 2 * padding;
            return this;
        }

        public Builder padding(Function<Builder, Double> paddingMapper) {
            return padding(paddingMapper.apply(this));
        }


        public Builder transform(Double factor, Coordinate offset) {
            this.x = this.x * factor + offset.getX();
            this.y = this.y * factor + offset.getY();
            this.width = this.width * factor;
            this.height = this.height * factor;
            return this;
        }

        public Builder x(Double x) {
            this.x = x;
            return this;
        }

        public Builder x(Function<NodeProperties, Double> mapper) {
            return x(mapper.apply(this));
        }

        public Builder y(Double y) {
            this.y = y;
            return this;
        }

        public Builder y(Function<NodeProperties, Double> mapper) {
            return y(mapper.apply(this));
        }

        public Builder width(Double width) {
            this.width = width;
            return this;
        }

        public Builder width(Function<NodeProperties, Double> mapper) {
            return width(mapper.apply(this));
        }

        public Builder height(Double height) {
            this.height = height;
            return this;
        }

        public Builder height(Function<NodeProperties, Double> mapper) {
            return height(mapper.apply(this));
        }

        @Override
        public Double getX() {
            return x;
        }

        @Override
        public Double getY() {
            return y;
        }

        @Override
        public Double getWidth() {
            return width;
        }

        @Override
        public Double getHeight() {
            return height;
        }

        public Coordinate getCenter() {
            return CoordinateFactory.get(x + width / 2d, y + height / 2d);
        }

        public NodeProperties build() {
            return NodePropertiesFactory.get(x, y, width, height);
        }
    }


}
