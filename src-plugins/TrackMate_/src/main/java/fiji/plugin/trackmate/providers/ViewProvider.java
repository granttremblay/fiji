package fiji.plugin.trackmate.providers;

import ij.ImagePlus;
import ij3d.Content;
import ij3d.ContentCreator;
import ij3d.Image3DUniverse;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.exception.ImgLibException;

import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackMateModel;
import fiji.plugin.trackmate.util.TMUtils;
import fiji.plugin.trackmate.visualization.TrackMateModelView;
import fiji.plugin.trackmate.visualization.hyperstack.HyperStackDisplayer;
import fiji.plugin.trackmate.visualization.threedviewer.SpotDisplayer3D;

public class ViewProvider {

	/** The view names, in the order they will appear in the GUI.
	 * These names will be used as keys to access relevant view classes.  */
	protected List<String> names;
	protected final TrackMateModel model;
	protected final Settings settings;
	protected final SelectionModel selectionModel;

	/*
	 * BLANK CONSTRUCTOR
	 */

	/**
	 * This provider provides the GUI with the model views currently available in the 
	 * TrackMate trackmate. Each view is identified by a key String, which can be used 
	 * to retrieve new instance of the view.
	 * <p>
	 * If you want to add custom views to TrackMate, a simple way is to extend this
	 * factory so that it is registered with the custom views and provide this 
	 * extended factory to the {@link TrackMate} trackmate.
	 */
	public ViewProvider(TrackMateModel model, Settings settings, SelectionModel selectionModel) {
		this.model = model;
		this.settings = settings;
		this.selectionModel = selectionModel;
		registerViews();
	}


	/*
	 * METHODS
	 */

	/**
	 * Register the standard views shipped with TrackMate.
	 */
	protected void registerViews() {
		// Names
		names = new ArrayList<String>(2);
		names.add(HyperStackDisplayer.NAME);
		names.add(SpotDisplayer3D.NAME);
	}

	/**
	 * Returns a new instance of the target view identified by the key parameter. 
	 * If the key is unknown to this factory, <code>null</code> is returned. 
	 */
	public TrackMateModelView getView(String key) {
		if (key == HyperStackDisplayer.NAME) {

			ImagePlus imp = settings.imp;
			return new HyperStackDisplayer(model, selectionModel, imp);
			
		} else if (key == SpotDisplayer3D.NAME) {
			

			Image3DUniverse universe = new Image3DUniverse();
			ImagePlus imp = settings.imp;
			if (null != imp) {
				//			if (!settings.imp.isVisible())
				//				settings.imp.show();
				ImagePlus[] images;
				try {
					images = TMUtils.makeImageForViewer(settings);
					final Content imageContent = ContentCreator.createContent(
							imp.getShortTitle(), 
							images, 
							Content.VOLUME, 
							SpotDisplayer3D.DEFAULT_RESAMPLING_FACTOR, 
							0,
							null, 
							SpotDisplayer3D.DEFAULT_THRESHOLD, 
							new boolean[] {true, true, true});
					universe.addContentLater(imageContent);	
				} catch (ImgLibException e) {
					e.printStackTrace();
				}
			}
			
			return new SpotDisplayer3D(model, selectionModel, universe);
		} else {
			return null;
		}
	}

	/**
	 * @return a list of the view names available through this factory.
	 */
	public List<String> getAvailableViews() {
		return names;
	}

	/**
	 * @return the html String containing a descriptive information about the target view,
	 * or <code>null</code> if it is unknown to this factory.
	 */
	public String getInfoText(String key) {
		int index = names.indexOf(key);
		if (index < 0) {
			return null;
		}
		switch (index) {
		case 0:
			return HyperStackDisplayer.INFO_TEXT;
		case 1:
			return SpotDisplayer3D.INFO_TEXT;
		default:
			return null;
		}
	}

}
