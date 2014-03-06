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
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import ru.desu.home.isef.services.auth.AuthenticationService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SidebarAjaxbasedController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    @Wire
    Grid fnList;

    @WireVariable
    SidebarPageConfig sidebarPageConfig;
    @WireVariable
    AuthenticationService authService;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        //to initial view after view constructed.
        Rows rows = fnList.getRows();

        for (SidebarPage page : sidebarPageConfig.getPages()) {
            Row row = constructSidebarRow(page.getName(), page.getLabel(), page.getIconUri(), page.getUri());
            if (!page.getName().startsWith("admin") || authService.getUserCredential().hasRole("admin")) {
                rows.appendChild(row);
            }
        }
    }

    private Row constructSidebarRow(final String name, String label, String imageSrc, final String locationUri) {

        //construct component and hierarchy
        final Row row = new Row();
        Image image = new Image(imageSrc);
        Label lab = new Label(label);

        if ((name.startsWith("myTask") || name.startsWith("admin")) && !name.equals("myTasks") && !name.equals("admin")) {
            row.setVisible(false);
            //row.setAction("show: slideDown;hide: slideUp");
            row.setAttribute("name", name);
            Image arrow_image = new Image("/imgs/arrow.png");
            row.appendChild(arrow_image);
            
            Hlayout lay = new Hlayout();
            lay.appendChild(image);
            lay.appendChild(lab);
            
            row.appendChild(lay);
        } else {
            row.appendChild(image);
            row.appendChild(lab);
        }

        //set style attribute
        row.setSclass("sidebar-fn");

        //new and register listener for events
        EventListener<Event> onActionListener = new SerializableEventListener<Event>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onEvent(Event event) throws Exception {

                if (locationUri.equals("MY_TASKS")) {
                    Rows rows = fnList.getRows();
                    for (Component comp : rows.getChildren()) {
                        if (comp instanceof Row) {
                            Row r = (Row) comp;
                            String attr = (String) r.getAttribute("name");
                            if (attr != null && attr.startsWith("myTask")) {
                                r.setVisible(true);
                            }
                            if (attr != null && !attr.startsWith("myTask")) {
                                r.setVisible(false);
                            }
                        }
                    }
                    return;
                } else if (locationUri.equals("ADMIN")) {
                    Rows rows = fnList.getRows();
                    for (Component comp : rows.getChildren()) {
                        if (comp instanceof Row) {
                            Row r = (Row) comp;
                            String attr = (String) r.getAttribute("name");
                            if (attr != null && attr.startsWith("admin")) {
                                r.setVisible(true);
                            }
                            if (attr != null && !attr.startsWith("admin")) {
                                r.setVisible(false);
                            }
                        }
                    }
                    return;
                } else if (!name.startsWith("myTask") && !name.startsWith("admin")) {
                    Rows rows = fnList.getRows();
                    for (Component comp : rows.getChildren()) {
                        if (comp instanceof Row) {
                            Row r = (Row) comp;
                            String attr = (String) r.getAttribute("name");
                            if (attr != null && (attr.startsWith("myTask")||attr.startsWith("admin"))) {
                                r.setVisible(false);
                            }
                        }
                    }
                }

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
                    Rows rows = fnList.getRows();
                    for (Component comp : rows.getChildren()) {
                        if (comp instanceof Row) {
                            Row r = (Row) comp;
                            r.setSclass("sidebar-fn");
                        }
                    }
                    row.setSclass("sidebar-fn current-fn");

                    //advance bookmark control, 
                    //bookmark with a prefix
                    getPage().getDesktop().setBookmark("p_" + name);
                }
            }
        };
        row.addEventListener(Events.ON_CLICK, onActionListener);

        return row;
    }

}
