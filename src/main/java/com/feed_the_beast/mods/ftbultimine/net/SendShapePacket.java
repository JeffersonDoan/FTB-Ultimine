package com.feed_the_beast.mods.ftbultimine.net;

import com.feed_the_beast.mods.ftbultimine.FTBUltimine;
import com.feed_the_beast.mods.ftbultimine.shape.Shape;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class SendShapePacket
{
	public static Shape current = null;

	private final Shape shape;
	private final List<BlockPos> blocks;

	public SendShapePacket(Shape s, List<BlockPos> b)
	{
		shape = s;
		blocks = b;
	}

	public SendShapePacket(PacketBuffer buf)
	{
		shape = Shape.get(buf.readString(Short.MAX_VALUE));
		int s = buf.readVarInt();
		blocks = new ArrayList<>(s);

		for (int i = 0; i < s; i++)
		{
			blocks.add(buf.readBlockPos());
		}
	}

	public void write(PacketBuffer buf)
	{
		buf.writeString(shape.getName(), Short.MAX_VALUE);
		buf.writeVarInt(blocks.size());

		for (BlockPos pos : blocks)
		{
			buf.writeBlockPos(pos);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> {
			current = shape;
			
			if (FTBUltimineConfig.renderOutline) {
				FTBUltimine.instance.proxy.setShape(blocks);
			}
		});

		context.get().setPacketHandled(true);
	}
}
