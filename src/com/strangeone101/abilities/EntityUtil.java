package com.strangeone101.abilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityUtil {
	public static Map<Class<? extends Entity>, BoundingBox> boxesPerEntity = new HashMap<Class<? extends Entity>, BoundingBox>();
	
	
	public static class BoundingBox {

        private final Vector point1;
        private final Vector point2;

        BoundingBox(Vector point1, Vector point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        public boolean contains(Vector vector, Entity beingTested) {
        	vector.setX(vector.getX() - beingTested.getLocation().getX());
        	vector.setY(vector.getY() - beingTested.getLocation().getY());
        	vector.setZ(vector.getZ() - beingTested.getLocation().getZ());
        	
            boolean inX = containsAxis(point1.getX(), point2.getX(), vector.getX());
            boolean inY = containsAxis(point1.getY(), point2.getY(), vector.getY());
            boolean inZ = containsAxis(point1.getZ(), point2.getZ(), vector.getZ());
            return inX && inY && inZ;
        }

        public boolean inRadius(Vector vector, double radius, Entity beingTested) {
        	vector.setX(vector.getX() - beingTested.getLocation().getX());
        	vector.setY(vector.getY() - beingTested.getLocation().getY());
        	vector.setZ(vector.getZ() - beingTested.getLocation().getZ());
        	
            boolean inX = inRadiusAxis(point1.getX(), point2.getX(), vector.getX(), radius);
            boolean inY = inRadiusAxis(point1.getY(), point2.getY(), vector.getY(), radius);
            boolean inZ = inRadiusAxis(point1.getZ(), point2.getZ(), vector.getZ(), radius);
            return inX && inY && inZ;
        }

        public Vector getPoint1() {
            return point1;
        }

        public Vector getPoint2() {
            return point2;
        }

        private boolean containsAxis(double point1, double point2, double testPoint) {
            return point1 > point2 ?
                    point1 > testPoint && testPoint > point2 :
                    point1 < testPoint && testPoint < point2;
        }

        private boolean inRadiusAxis(double point1, double point2, double testPoint, double radius) {
            return point1 > point2 ?
                    (point1 > (testPoint - radius) && (testPoint + radius) > point2) :
                    (point1 < (testPoint + radius) && (testPoint - radius) < point2);
        }

    }

	private static final String version;
	private static Class<?> clazzCraftEntity;
	private static Method getHandle;
	private static Method getBoundingBox;
	
	static {
		version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		try {
			clazzCraftEntity = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftEntity");
			getHandle = clazzCraftEntity.getMethod("getHandle");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

    public static BoundingBox getBoundingBox(Entity entity) {
    	if (boxesPerEntity.containsKey(entity.getClass())) return boxesPerEntity.get(entity.getClass());
    	
        try {
        	final Object craftEntity = getHandle.invoke(clazzCraftEntity.cast(entity));
        	final Class<?> clazzHandle = craftEntity.getClass();
            getBoundingBox = clazzHandle.getMethod("getBoundingBox");
            final Object boundingBox = getBoundingBox.invoke(craftEntity);
            
            final Class<?> clazzBoundingBox = boundingBox.getClass();
            double fieldA = (double) clazzBoundingBox.getDeclaredField("a").get(boundingBox);
            double fieldB = (double) clazzBoundingBox.getDeclaredField("b").get(boundingBox);
            double fieldC = (double) clazzBoundingBox.getDeclaredField("c").get(boundingBox);
            double fieldD = (double) clazzBoundingBox.getDeclaredField("d").get(boundingBox);
            double fieldE = (double) clazzBoundingBox.getDeclaredField("e").get(boundingBox);
            double fieldF = (double) clazzBoundingBox.getDeclaredField("f").get(boundingBox);
            
            fieldA -= entity.getLocation().getX();
            fieldB -= entity.getLocation().getY();
            fieldC -= entity.getLocation().getZ();
            fieldD -= entity.getLocation().getX();
            fieldE -= entity.getLocation().getY();
            fieldF -= entity.getLocation().getZ();

            BoundingBox box = new BoundingBox(
                    new Vector(fieldA, fieldB, fieldC),
                    new Vector(fieldD, fieldE, fieldF)
            );
            
            boxesPerEntity.put(entity.getClass(), box);
            return box;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to get BoundingBox: ", ex);
        }
    }
    
    public static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
		List<Entity> entities = new ArrayList<Entity>();
		World world = location.getWorld();

		// To find chunks we use chunk coordinates (not block coordinates!)
		int smallX = (int) (location.getX() - radius) >> 4;
		int bigX = (int) (location.getX() + radius) >> 4;
		int smallZ = (int) (location.getZ() - radius) >> 4;
		int bigZ = (int) (location.getZ() + radius) >> 4;

		for (int x = smallX; x <= bigX; x++) {
			for (int z = smallZ; z <= bigZ; z++) {
				if (world.isChunkLoaded(x, z)) {
					entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
				}
			}
		}

		Iterator<Entity> entityIterator = entities.iterator();
		while (entityIterator.hasNext()) {
			Entity e = entityIterator.next();
			if (e.getWorld().equals(location.getWorld()) && !getBoundingBox(e).inRadius(location.toVector(), radius, e)) {
				entityIterator.remove();
			} else if ((e instanceof Player && ((Player) e).getGameMode().equals(GameMode.SPECTATOR)) || !(e instanceof LivingEntity)) {
				entityIterator.remove();
			}
		}
		
		return entities;
    }
}
