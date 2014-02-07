package ru.desu.home.isef.controller.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SerializableEventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SidebarAjaxbasedController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Grid fnList;

    @WireVariable
    SidebarPageConfig sidebarPageConfig;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //to initial view after view constructed.
        Rows rows = fnList.getRows();

        for (SidebarPage page : sidebarPageConfig.getPages()) {
            Row row = constructSidebarRow(page.getName(), page.getLabel(), page.getIconUri(), page.getUri());
            rows.appendChild(row);
        }
    }

    private Row constructSidebarRow(final String name, String label, String imageSrc, final String locationUri) {

        //construct component and hierarchy
        Row row = new Row();
        Image image = new Image(imageSrc);
        Label lab = new Label(label);

        row.appendChild(image);
        row.appendChild(lab);

        //set style attribute
        row.setSclass("sidebar-fn");

        //new and register listener for events
        EventListener<Event> onActionListener = new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(Event event) throws Exception {
                //redirect current url to new location
                if (locationUri.startsWith("http")) {
                    //open a new browser tab
                    Executions.getCurrent().sendRedirect(locationUri);
                } else if (locationUri.equals("HOME_PAGE")) {
                    String host = Executions.getCurrent().getServerName();
                    int port = Executions.getCurrent().getServerPort();
                    Executions.getCurrent().sendRedirect("http://" + host + ":" + port + "/");
                } else if (locationUri.equals("WORK_PAGE")) {
                    String host = Executions.getCurrent().getServerName();
                    int port = Executions.getCurrent().getServerPort();
                    Executions.getCurrent().sendRedirect("http://" + host + ":" + port + "/work/");
                } else {
                    //use iterable to find the first include only
                    Include include = (Include) Selectors.iterable(fnList.getPage(), "#mainInclude")
                            .iterator().next();
                    include.setSrc(locationUri);

                    //advance bookmark control, 
                    //bookmark with a prefix
                    if (name != null) {
                        getPage().getDesktop().setBookmark("p_" + name);
                    }
                }
            }
        };
        row.addEventListener(Events.ON_CLICK, onActionListener);

        return row;
    }

}
