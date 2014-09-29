package de.bund.bfr.gwt.krise.client;

/*******************************************************************************
 * Copyright 2009, 2010 Lars Grammel 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 *******************************************************************************/

import org.thechiselgroup.choosel.protovis.client.Link;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVColor;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVForceLayout;
import org.thechiselgroup.choosel.protovis.client.PVLink;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVNode;
import org.thechiselgroup.choosel.protovis.client.PVOrdinalScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.ProtovisWidget;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Protovis/GWT implementation of <a
 * href="http://vis.stanford.edu/protovis/ex/force.html">Protovis Force-Directed
 * Layout example</a>.
 * 
 * @author Lars Grammel
 */
public class ForceDirectedLayout extends ProtovisWidget {

	private PVForceLayout force;

	@Override
	public Widget asWidget() {
		return this;
	}

	private void createVisualization(MyNodes[] nodes, Link[] links) {
		int w = 800;
		int h = 600;

		PVPanel vis = getPVPanel().width(w).height(h).fillStyle("white").event(PV.Event.MOUSEDOWN, PV.Behavior.pan()).event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom());

		force = vis.add(PV.Layout.Force()).nodes(new MyNodeAdapter(), nodes).links(links);

		//force.link().add(PV.Line).event(PV.Event.CLICK, PV.Behavior.select());
		force.link().add(PV.Line)
		//.add(PV.Dot)
		.event(PV.Event.DOUBLE_CLICK, new PVEventHandler() {
			@Override
			public void onEvent(com.google.gwt.user.client.Event e, String pvEventType, JsArgs args) {
				
				final DialogBox db = new DialogBox();
				db.setText(e + "\t" + pvEventType);
				db.setAnimationEnabled(true);
				db.setGlassEnabled(true);
		        Button ok = new Button("OK");
		        ok.addClickHandler(new ClickHandler() {
		           public void onClick(ClickEvent event) {
		        	   db.hide();
		           }
		        });
		        //EventTarget svg = e.getEventTarget(); 
		        //Link link = (Link) args.getObject(0);
		        PVLink link = args.getObject(1); // 0 is PVNodes
		        //Link link = args.getObject(0);

		        Label label = new Label(link.sourceNode().nodeName() + "\t" + link.targetNode().nodeName() + "\t" + link.linkValue());

		        VerticalPanel panel = new VerticalPanel();
		        panel.setHeight("100");
		        panel.setWidth("300");
		        panel.setSpacing(10);
		        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		        panel.add(label);
		        panel.add(ok);

		        db.setWidget(panel);
		        int left = Window.getClientWidth()/ 2;
	            int top = Window.getClientHeight()/ 2;
	            db.setPopupPosition(left, top);
	   		    db.show();

			}
		});

		force.node().add(PV.Dot).size(new JsDoubleFunction() {
			public double f(JsArgs args) {
				PVNode d = args.getObject();
				PVMark _this = args.<PVMark> getThis();
				return (d.linkDegree() + 4) * (Math.pow(_this.scale(), -1.5));
			}
		}).fillStyle(new JsFunction<PVColor>() {
			private PVOrdinalScale colors = PV.Colors.category19();

			public PVColor f(JsArgs args) {
				PVNode d = args.getObject();
				if (d.fix()) {
					return PV.color("brown");
				}
				return colors.fcolor(d.<MyNodes> object().getColor());
			}
		}).strokeStyle(new JsFunction<PVColor>() {
			public PVColor f(JsArgs args) {
				PVDot _this = args.getThis();
				return _this.fillStyle().darker();
			}
		}).lineWidth(1).title(new JsStringFunction() {
			public String f(JsArgs args) {
				PVNode d = args.getObject();
				return d.nodeName();
			}
		}).event(PV.Event.MOUSEDOWN, PV.Behavior.drag()).event(PV.Event.DRAG, force);
	}

	private MyNodes[] getNodes() {
		int numNodes = 10;
		MyNodes[] result = new MyNodes[numNodes];
		for (int i = 0; i < numNodes; i++) {
			result[i] = new MyNodes("node" + i, i, PV.color("red"));
		}
		return result;
	}

	private Link[] getLinks() {
		int numNodes = 10;
		Link[] result = new Link[11];
		for (int i = 0; i < 10; i++) {
			result[i] = new Link(i, i == numNodes - 1 ? 0 : i + 1, 6.0);
		}
		result[10] = new Link(0, 1, 3.0);
		return result;
	}

	protected void onAttach() {
		super.onAttach();
		initPVPanel();
		createVisualization(getNodes(), getLinks());
		getPVPanel().render();
		asWidget().getElement().getStyle().setProperty("border", "1px solid #aaa");
	}

	@Override
	protected void onDetach() {
		// stop the force directed layout
		if (force != null) {
			force.stop();
		}

		super.onDetach();
	}

	public String toString() {
		return "Force-Directed Layout";
	}

}