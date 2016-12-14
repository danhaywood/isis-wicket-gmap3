package org.isisaddons.wicket.gmap3.cpt.ui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.metamodel.spec.SpecificationLoader;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.viewer.wicket.model.models.EntityCollectionModel;
import org.apache.isis.viewer.wicket.ui.CollectionContentsAsFactory;
import org.apache.isis.viewer.wicket.ui.ComponentFactoryAbstract;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.isisaddons.wicket.gmap3.cpt.applib.Routeable;

public class CollectionOfEntitiesAsRouteablesFactory extends ComponentFactoryAbstract implements CollectionContentsAsFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7940581460663515229L;

	private static final String ID_MAP = "map";

    private boolean determinedWhetherInternetReachable;
    private boolean internetReachable;

    public CollectionOfEntitiesAsRouteablesFactory() {
        super(ComponentType.COLLECTION_CONTENTS, ID_MAP);
    }

    @Override
    public ApplicationAdvice appliesTo(IModel<?> model) {
        
        if(!internetReachable()) {
            return ApplicationAdvice.DOES_NOT_APPLY;
        }
        
        if (!(model instanceof EntityCollectionModel)) {
            return ApplicationAdvice.DOES_NOT_APPLY;
        }

        EntityCollectionModel entityCollectionModel = (EntityCollectionModel) model;

        ObjectSpecification typeOfSpec = entityCollectionModel.getTypeOfSpecification();
        ObjectSpecification routeableSpec = getSpecificationLoader().loadSpecification(Routeable.class);
        return appliesIf(typeOfSpec.isOfType(routeableSpec));
    }

    private boolean internetReachable() {
        if(!determinedWhetherInternetReachable) {
            internetReachable = isInternetReachable();
            determinedWhetherInternetReachable = true;
        }
        return internetReachable;
    }

    /**
     * Tries to retrieve some content, 1 second timeout.
     */
    private static boolean isInternetReachable()
    {
        try {
            final URL url = new URL("http://www.google.com");
            final HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setConnectTimeout(1000);
            urlConnect.getContent();
            urlConnect.disconnect();
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public Component createComponent(String id, IModel<?> model) {
        EntityCollectionModel collectionModel = (EntityCollectionModel) model;
        return new CollectionOfEntitiesAsRouteables(id, collectionModel);
    }

    protected SpecificationLoader getSpecificationLoader() {
        return IsisContext.getSpecificationLoader();
    }

    @Override
    public IModel<String> getTitleLabel() {
        return Model.of("Map");
    }

    @Override
    public IModel<String> getCssClass() {
        return Model.of("fa fa-map-marker");
    }
}
