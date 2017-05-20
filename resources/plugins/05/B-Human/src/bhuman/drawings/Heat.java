package bhuman.drawings;

import bhuman.message.BHumanMessage;
import bhuman.message.messages.RobotHealth;
import com.jogamp.opengl.GL2;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import teamcomm.data.RobotState;
import teamcomm.gui.Camera;
import teamcomm.gui.drawings.Image;
import teamcomm.gui.drawings.PerPlayer;
import teamcomm.gui.drawings.TextureLoader;

/**
 * Custom drawing for visualizing when a robot's joints are hot.
 *
 * @author Felix Thielke
 */
public class Heat extends PerPlayer {

    private static final int HEAT_TEMPERATURE = 70;
    private static final int FIRE_TEMPERATURE = 90;
    private static final int FIRE_EXCLAMATION_MARK_TEMPERATURE = 100;

    @Override
    public void draw(final GL2 gl, final RobotState rs, final Camera camera) {
        if (rs.getLastMessage() != null
                && rs.getLastMessage().valid
                && rs.getLastMessage() instanceof BHumanMessage) {
            final BHumanMessage msg = (BHumanMessage) rs.getLastMessage();
            final RobotHealth health;
            if (msg.message.queue != null && (health = msg.message.queue.getCachedMessage(RobotHealth.class)) != null && health.maxJointTemperature >= HEAT_TEMPERATURE) {
                gl.glPushMatrix();
                gl.glTranslatef(msg.pose[0] / 1000.f, msg.pose[1] / 1000.f, 1);
                camera.turnTowardsCamera(gl);
                final String filename;
                if (health.maxJointTemperature >= FIRE_EXCLAMATION_MARK_TEMPERATURE) {
                    filename = "fire!_icon.png";
                } else if (health.maxJointTemperature >= FIRE_TEMPERATURE) {
                    filename = "fire_icon.png";
                } else {
                    filename = "heat_icon.png";
                }
                try {
                    final File f = new File("plugins/" + (rs.getTeamNumber() < 10 ? "0" + rs.getTeamNumber() : String.valueOf(rs.getTeamNumber())) + "/resources/" + filename).getAbsoluteFile();
                    Image.drawImage(gl, TextureLoader.getInstance().loadTexture(gl, f), 0, 0, 0.2f);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Error loading texture: " + ex.getMessage(),
                            ex.getClass().getSimpleName(),
                            JOptionPane.ERROR_MESSAGE);
                }
                gl.glPopMatrix();
            }
        }
    }

    @Override
    public boolean hasAlpha() {
        return true;
    }

    @Override
    public int getPriority() {
        return 9;
    }

}