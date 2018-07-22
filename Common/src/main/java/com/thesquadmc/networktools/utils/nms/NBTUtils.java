package com.thesquadmc.networktools.utils.nms;

import com.google.common.primitives.Primitives;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NBTUtils {

    private static HashMap<String, Class<?>> classCache;
    private static HashMap<String, Method> methodCache;
    private static HashMap<Class<?>, Constructor<?>> constructorCache;
    private static HashMap<Class<?>, Class<?>> NBTClasses;
    private static HashMap<Class<?>, Field> NBTTagFieldCache;
    private static Field NBTListData;
    private static Field NBTCompoundMap;
    private static String version;

    static {
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        classCache = new HashMap<>();
        try {
            classCache.put("NBTBase", Class.forName("net.minecraft.server." + version + "." + "NBTBase"));
            classCache.put("NBTTagCompound", Class.forName("net.minecraft.server." + version + "." + "NBTTagCompound"));
            classCache.put("NBTTagList", Class.forName("net.minecraft.server." + version + "." + "NBTTagList"));
            classCache.put("NBTBase", Class.forName("net.minecraft.server." + version + "." + "NBTBase"));

            classCache.put("ItemStack", Class.forName("net.minecraft.server." + version + "." + "ItemStack"));
            classCache.put("CraftItemStack", Class.forName("org.bukkit.craftbukkit." + version + ".inventory." + "CraftItemStack"));

            classCache.put("Entity", Class.forName("net.minecraft.server." + version + "." + "Entity"));
            classCache.put("CraftEntity", Class.forName("org.bukkit.craftbukkit." + version + ".entity." + "CraftEntity"));
            classCache.put("EntityLiving", Class.forName("net.minecraft.server." + version + "." + "EntityLiving"));

            classCache.put("CraftWorld", Class.forName("org.bukkit.craftbukkit." + version + "." + "CraftWorld"));
            classCache.put("CraftBlockState", Class.forName("org.bukkit.craftbukkit." + version + ".block." + "CraftBlockState"));
            classCache.put("BlockPosition", Class.forName("net.minecraft.server." + version + "." + "BlockPosition"));
            classCache.put("TileEntity", Class.forName("net.minecraft.server." + version + "." + "TileEntity"));
            classCache.put("World", Class.forName("net.minecraft.server." + version + "." + "World"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        NBTClasses = new HashMap<Class<?>, Class<?>>();
        try {
            NBTClasses.put(Byte.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagByte"));
            NBTClasses.put(String.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagString"));
            NBTClasses.put(Double.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagDouble"));
            NBTClasses.put(Integer.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagInt"));
            NBTClasses.put(Long.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagLong"));
            NBTClasses.put(Short.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagShort"));
            NBTClasses.put(Float.class, Class.forName("net.minecraft.server." + version + "." + "NBTTagFloat"));
            NBTClasses.put(Class.forName("[B"), Class.forName("net.minecraft.server." + version + "." + "NBTTagByteArray"));
            NBTClasses.put(Class.forName("[I"), Class.forName("net.minecraft.server." + version + "." + "NBTTagIntArray"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        methodCache = new HashMap<String, Method>();
        try {
            methodCache.put("get", getNMSClass("NBTTagCompound").getMethod("get", String.class));
            methodCache.put("set", getNMSClass("NBTTagCompound").getMethod("set", String.class, getNMSClass("NBTBase")));
            methodCache.put("hasKey", getNMSClass("NBTTagCompound").getMethod("hasKey", String.class));
            methodCache.put("setIndex", getNMSClass("NBTTagList").getMethod("a", int.class, getNMSClass("NBTBase")));
            methodCache.put("add", getNMSClass("NBTTagList").getMethod("add", getNMSClass("NBTBase")));

            methodCache.put("hasTag", getNMSClass("ItemStack").getMethod("hasTag"));
            methodCache.put("getTag", getNMSClass("ItemStack").getMethod("getTag"));
            methodCache.put("setTag", getNMSClass("ItemStack").getMethod("setTag", getNMSClass("NBTTagCompound")));
            methodCache.put("asNMSCopy", getNMSClass("CraftItemStack").getMethod("asNMSCopy", ItemStack.class));
            methodCache.put("asBukkitCopy", getNMSClass("CraftItemStack").getMethod("asBukkitCopy", getNMSClass("ItemStack")));

            methodCache.put("getEntityHandle", getNMSClass("CraftEntity").getMethod("getHandle"));
            methodCache.put("getEntityTag", getNMSClass("Entity").getMethod("c", getNMSClass("NBTTagCompound")));
            methodCache.put("setEntityTag", getNMSClass("Entity").getMethod("f", getNMSClass("NBTTagCompound")));

            methodCache.put("setTileTag", getNMSClass("TileEntity").getMethod("a", getNMSClass("NBTTagCompound")));
            methodCache.put("getTileEntity", getNMSClass("World").getMethod("getTileEntity", getNMSClass("BlockPosition")));
            methodCache.put("getWorldHandle", getNMSClass("CraftWorld").getMethod("getHandle"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            methodCache.put("getTileTag", getNMSClass("TileEntity").getMethod("save", getNMSClass("NBTTagCompound")));
        } catch (NoSuchMethodException exception) {
            try {
                methodCache.put("getTileTag", getNMSClass("TileEntity").getMethod("b", getNMSClass("NBTTagCompound")));
            } catch (Exception exception2) {
                exception2.printStackTrace();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        constructorCache = new HashMap<Class<?>, Constructor<?>>();
        try {
            constructorCache.put(getNBTTag(Byte.class), getNBTTag(Byte.class).getConstructor(byte.class));
            constructorCache.put(getNBTTag(String.class), getNBTTag(String.class).getConstructor(String.class));
            constructorCache.put(getNBTTag(Double.class), getNBTTag(Double.class).getConstructor(double.class));
            constructorCache.put(getNBTTag(Integer.class), getNBTTag(Integer.class).getConstructor(int.class));
            constructorCache.put(getNBTTag(Long.class), getNBTTag(Long.class).getConstructor(long.class));
            constructorCache.put(getNBTTag(Float.class), getNBTTag(Float.class).getConstructor(float.class));
            constructorCache.put(getNBTTag(Short.class), getNBTTag(Short.class).getConstructor(short.class));
            constructorCache.put(getNBTTag(Class.forName("[B")), getNBTTag(Class.forName("[B")).getConstructor(Class.forName("[B")));
            constructorCache.put(getNBTTag(Class.forName("[I")), getNBTTag(Class.forName("[I")).getConstructor(Class.forName("[I")));

            constructorCache.put(getNMSClass("BlockPosition"), getNMSClass("BlockPosition").getConstructor(int.class, int.class, int.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        NBTTagFieldCache = new HashMap<Class<?>, Field>();
        try {
            for (Class<?> clazz : NBTClasses.values()) {
                Field data = clazz.getDeclaredField("data");
                data.setAccessible(true);
                NBTTagFieldCache.put(clazz, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NBTListData = getNMSClass("NBTTagList").getDeclaredField("list");
            NBTListData.setAccessible(true);
            NBTCompoundMap = getNMSClass("NBTTagCompound").getDeclaredField("map");
            NBTCompoundMap.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getPrimitiveClass(Class<?> clazz) {
        return Primitives.unwrap(clazz);
    }

    public static Class<?> getNBTTag(Class<?> primitiveType) {
        if (NBTClasses.containsKey(primitiveType))
            return NBTClasses.get(primitiveType);
        return primitiveType;
    }

    public static Object getNBTVar(Object object) {
        if (object == null) return null;
        Class<?> clazz = object.getClass();
        try {
            if (NBTTagFieldCache.containsKey(clazz)) {
                System.out.println(NBTTagFieldCache.get(clazz).get(object));
                return NBTTagFieldCache.get(clazz).get(object);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(String name) {
        return methodCache.containsKey(name) ? methodCache.get(name) : null;
    }

    public static Constructor<?> getConstructor(Class<?> clazz) {
        return constructorCache.containsKey(clazz) ? constructorCache.get(clazz) : null;
    }

    public static Class<?> getNMSClass(String name) {
        if (classCache.containsKey(name)) {
            return classCache.get(name);
        }

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getItemTag(ItemStack item, Object... keys) {
        try {
            Object stack = null;
            stack = getMethod("asNMSCopy").invoke(null, item);

            Object tag = null;

            if (getMethod("hasTag").invoke(stack).equals(true)) {
                tag = getMethod("getTag").invoke(stack);
            } else {
                tag = getNMSClass("NBTTagCompound").newInstance();
            }

            return getTag(tag, keys);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static ItemStack setItemTag(ItemStack item, Object value, Object... keys) {
        try {
            Object stack = getMethod("asNMSCopy").invoke(null, item);

            Object tag = null;

            if (getMethod("hasTag").invoke(stack).equals(true)) {
                tag = getMethod("getTag").invoke(stack);
            } else {
                tag = getNMSClass("NBTTagCompound").newInstance();
            }

            setTag(tag, value, keys);
            getMethod("setTag").invoke(stack, tag);
            return (ItemStack) getMethod("asBukkitCopy").invoke(null, stack);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static Object getEntityTag(Entity entity, Object... keys) {
        try {
            Object NMSEntity = getMethod("getEntityHandle").invoke(entity);

            Object tag = getNMSClass("NBTTagCompound").newInstance();

            getMethod("getEntityTag").invoke(NMSEntity, tag);

            return getTag(tag, keys);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void setEntityTag(Entity entity, Object value, Object... keys) {
        try {
            Object NMSEntity = getMethod("getEntityHandle").invoke(entity);

            Object tag = getNMSClass("NBTTagCompound").newInstance();

            getMethod("getEntityTag").invoke(NMSEntity, tag);

            setTag(tag, value, keys);

            getMethod("setEntityTag").invoke(NMSEntity, tag);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    public static Object getBlockTag(Block block, Object... keys) {
        try {
            if (!getNMSClass("CraftBlockState").isInstance(block.getState())) {
                return null;
            }

            Object tileEntity = getMethod("getTileEntity").invoke(block.getState());

            Object tag = getMethod("getTileTag").invoke(tileEntity, getNMSClass("NBTTagCompound").newInstance());

            return getTag(tag, keys);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void setBlockTag(Block block, Object value, Object... keys) {
        try {
            Location location = block.getLocation();

            Object blockPosition = getConstructor(getNMSClass("BlockPosition")).newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            Object nmsWorld = getMethod("getWorldHandle").invoke(location.getWorld());

            Object tileEntity = getMethod("getTileEntity").invoke(nmsWorld, blockPosition);

            Object tag = getMethod("getTileTag").invoke(tileEntity, getNMSClass("NBTTagCompound").newInstance());

            setTag(tag, value, keys);

            getMethod("setTileTag").invoke(tileEntity, tag);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private static void setTag(Object tag, Object value, Object... keys) throws Exception {
        Object notCompound = getConstructor(getNBTTag(value.getClass())).newInstance(value);

        Object compound = tag;
        for (int index = 0; index < keys.length; index++) {
            Object key = keys[index];
            if (index + 1 == keys.length) {
                if (key == null) {
                    getMethod("add").invoke(compound, notCompound);
                } else if (key instanceof Integer) {
                    getMethod("setIndex").invoke(compound, key, notCompound);
                } else {
                    getMethod("set").invoke(compound, key, notCompound);
                }
                break;
            }
            Object oldCompound = compound;
            if (key instanceof Integer) {
                compound = ((List<?>) NBTListData.get(compound)).get((int) key);
            } else if (key != null) {
                compound = getMethod("get").invoke(compound, (String) key);
            }
            if (compound == null || key == null) {
                if (keys[index + 1] == null || keys[index + 1] instanceof Integer) {
                    compound = getNMSClass("NBTTagList").newInstance();
                } else {
                    compound = getNMSClass("NBTTagCompound").newInstance();
                }
                if (oldCompound.getClass().getSimpleName().equals("NBTTagList")) {
                    getMethod("add").invoke(oldCompound, compound);
                } else {
                    getMethod("set").invoke(oldCompound, key, compound);
                }
            }
        }
    }

    private static Object getTag(Object tag, Object... keys) throws Exception {
        if (keys.length == 0) return getTags(tag);

        Object notCompound = tag;

        for (Object key : keys) {
            if (notCompound == null) return null;
            if (getNMSClass("NBTTagCompound").isInstance(notCompound)) {
                notCompound = getMethod("get").invoke(notCompound, (String) key);
            } else if (getNMSClass("NBTTagList").isInstance(notCompound)) {
                notCompound = ((List<?>) NBTListData.get(notCompound)).get((int) key);
            } else {
                return getNBTVar(notCompound);
            }
        }
        if (notCompound == null) return null;
        if (getNMSClass("NBTTagList").isInstance(notCompound)) {
            return getTags(notCompound);
        } else if (getNMSClass("NBTTagCompound").isInstance(notCompound)) {
            return getTags(notCompound);
        } else {
            return getNBTVar(notCompound);
        }
    }

    private static Object getTags(Object tag) {
        HashMap<Object, Object> tags = new HashMap<Object, Object>();
        try {
            if (getNMSClass("NBTTagCompound").isInstance(tag)) {
                Map<String, Object> tagCompound = (Map<String, Object>) NBTCompoundMap.get(tag);
                for (String key : tagCompound.keySet()) {
                    Object value = tagCompound.get(key);
                    if (getNMSClass("NBTTagEnd").isInstance(value)) continue;
                    tags.put(key, getTag(value));
                }
            } else if (getNMSClass("NBTTagList").isInstance(tag)) {
                List<Object> tagList = (List<Object>) NBTListData.get(tag);
                for (int index = 0; index < tagList.size(); index++) {
                    Object value = tagList.get(index);
                    if (getNMSClass("NBTTagEnd").isInstance(value)) continue;
                    tags.put(index, getTag(value));
                }
            } else {
                return getNBTVar(tag);
            }
            return tags;
        } catch (Exception e) {
            e.printStackTrace();
            return tags;
        }
    }

}
