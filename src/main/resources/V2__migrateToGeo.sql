CREATE EXTENSION IF NOT EXISTS postgis;

SELECT postgis_extensions_upgrade();

ALTER TABLE household
ADD location_geo geometry(POINT,4326);

CREATE INDEX household_geom_x ON household USING GIST (location_geo);

ALTER TABLE household
ADD CONSTRAINT enforce_geotype_geom CHECK (geometrytype(location_geo) = 'POINT'::text);

ALTER TABLE household
ADD CONSTRAINT enforce_longitude CHECK (ST_X(location_geo) <= 180.0 AND ST_X(location_geo) >= -180.0);

ALTER TABLE household
ADD CONSTRAINT enforce_latitude CHECK (ST_Y(location_geo) <= 90.0 AND ST_Y(location_geo) >= -90.0);


