import{_ as D}from"./index.8db9e24f.js";/* empty css                *//* empty css                *//* empty css               *//* empty css                */import{d as w,j as H,C as a,D as f,aE as r,aH as e,c7 as W,u as p,e as J,aM as g,aN as _,aK as k,aF as C,aI as t,G as q,bT as z,ah as K,bm as O,bn as Q,X as U,bl as X,c8 as Z,bc as tt,aW as et,aY as at,aJ as N,bw as R,a6 as nt,bu as ot,bO as S,c0 as st,c9 as A,ca as F,bY as I,b1 as lt,bv as it,bs as ct,b$ as rt}from"./arco.08ce5409.js";/* empty css                *//* empty css                *//* empty css                *//* empty css               *//* empty css               *//* empty css                *//* empty css                */import{a as dt,b as _t,c as pt}from"./list.6bf46b30.js";import{u as E}from"./request.b3ebd81c.js";/* empty css               *//* empty css                *//* empty css               */import{k as ut}from"./vue.74cbe595.js";import"./chart.ea45cf69.js";import"./loading.fe93ec0f.js";const ft={class:"card-wrap"},mt={key:2},yt=w({__name:"card-wrap",props:{loading:{type:Boolean,default:!1},title:{type:String,default:""},description:{type:String,default:""},actionType:{type:String,default:""},defaultValue:{type:Boolean,default:!1},openTxt:{type:String,default:""},closeTxt:{type:String,default:""},expiresText:{type:String,default:""},icon:{type:String,default:""},showTag:{type:Boolean,default:!0},tagText:{type:String,default:""},expires:{type:Boolean,default:!1},expiresTagText:{type:String,default:""}},setup(o){const l=o,[c,u]=ut(l.defaultValue),n=()=>{u()},x=H(l.expires),m=()=>{x.value=!1};return(d,i)=>{const y=z,v=K,s=O,T=Q,$=U,L=X,h=Z,V=tt,G=et,b=at;return a(),f("div",ft,[o.loading?(a(),r(y,{key:0,bordered:!1,hoverable:""},{default:e(()=>[W(d.$slots,"skeleton",{},void 0,!0)]),_:3})):(a(),r(y,{key:1,bordered:!1,hoverable:""},{actions:e(()=>[o.actionType==="switch"?(a(),r(G,{key:0,modelValue:p(c),"onUpdate:modelValue":i[0]||(i[0]=B=>J(c)?c.value=B:null)},null,8,["modelValue"])):o.actionType==="button"?(a(),r(V,{key:1},{default:e(()=>[x.value?(a(),r(b,{key:0,type:"outline",onClick:m},{default:e(()=>[g(_(o.expiresText),1)]),_:1})):(a(),f(k,{key:1},[p(c)?(a(),r(b,{key:0,onClick:n},{default:e(()=>[g(_(o.closeTxt),1)]),_:1})):p(c)?C("",!0):(a(),r(b,{key:1,type:"outline",onClick:n},{default:e(()=>[g(_(o.openTxt),1)]),_:1}))],64))]),_:1})):(a(),f("div",mt,[t(V,null,{default:e(()=>[t(b,{onClick:i[1]||(i[1]=B=>p(u)(!1))},{default:e(()=>[g(_(o.closeTxt),1)]),_:1}),t(b,{type:"primary",onClick:i[2]||(i[2]=B=>p(u)(!0))},{default:e(()=>[g(_(o.openTxt),1)]),_:1})]),_:1})]))]),default:e(()=>[t(V,{align:"start"},{default:e(()=>[o.icon?(a(),r(s,{key:0,size:24,style:{"margin-right":"8px","background-color":"#626aea"}},{default:e(()=>[t(v)]),_:1})):C("",!0),t(h,null,{title:e(()=>[t(T,{style:{"margin-right":"10px"}},{default:e(()=>[g(_(o.title),1)]),_:1}),o.showTag?(a(),f(k,{key:0},[p(c)&&x.value===!1?(a(),r(L,{key:0,size:"small",color:"green"},{icon:e(()=>[t($)]),default:e(()=>[q("span",null,_(o.tagText),1)]),_:1})):x.value?(a(),r(L,{key:1,size:"small",color:"red"},{icon:e(()=>[t($)]),default:e(()=>[q("span",null,_(o.expiresTagText),1)]),_:1})):C("",!0)],64)):C("",!0)]),description:e(()=>[g(_(o.description)+" ",1),W(d.$slots,"default",{},void 0,!0)]),_:3})]),_:3})]),_:3}))])}}});const P=D(yt,[["__scopeId","data-v-e2c91412"]]),gt={class:"list-wrap"},xt={class:"card-wrap empty-wrap"},ht=w({__name:"quality-inspection",setup(o){const l=new Array(3).fill({}),{loading:c,response:u}=E(dt,l);return(n,x)=>{const m=R,d=nt,i=ot,y=z,v=S,s=st,T=A,$=F,L=I;return a(),f("div",gt,[t(m,{class:"block-title",heading:6},{default:e(()=>[g(_(n.$t("cardList.tab.title.content")),1)]),_:1}),t(L,{class:"list-row",gutter:24},{default:e(()=>[t(v,{xs:12,sm:12,md:12,lg:6,xl:6,xxl:6,class:"list-col"},{default:e(()=>[q("div",xt,[t(y,{bordered:!1,hoverable:""},{default:e(()=>[t(i,{status:null,title:n.$t("cardList.content.action")},{icon:e(()=>[t(d,{style:{"font-size":"20px"}})]),_:1},8,["title"])]),_:1})])]),_:1}),(a(!0),f(k,null,N(p(u),h=>(a(),r(v,{key:h.id,class:"list-col",xs:12,sm:12,md:12,lg:6,xl:6,xxl:6},{default:e(()=>[t(P,{loading:p(c),title:h.title,description:h.description,"default-value":h.enable,"action-type":h.actionType,icon:h.icon,"open-txt":n.$t("cardList.content.inspection"),"close-txt":n.$t("cardList.content.delete"),"show-tag":!1},{skeleton:e(()=>[t($,{animation:!0},{default:e(()=>[t(T,{widths:["50%","50%","100%","40%"],rows:4}),t(T,{widths:["40%"],rows:1})]),_:1})]),default:e(()=>[t(s,{style:{"margin-top":"16px"},data:h.data,layout:"inline-horizontal",column:2},null,8,["data"])]),_:2},1032,["loading","title","description","default-value","action-type","icon","open-txt","close-txt"])]),_:2},1024))),128))]),_:1})])}}});const j=D(ht,[["__scopeId","data-v-93e41bf1"]]),vt={class:"list-wrap"},M=w({__name:"the-service",setup(o){const l=new Array(4).fill({}),{loading:c,response:u}=E(_t,l);return(n,x)=>{const m=R,d=A,i=F,y=S,v=I;return a(),f("div",vt,[t(m,{class:"block-title",heading:6},{default:e(()=>[g(_(n.$t("cardList.tab.title.service")),1)]),_:1}),t(v,{class:"list-row",gutter:24},{default:e(()=>[(a(!0),f(k,null,N(p(u),s=>(a(),r(y,{key:s.id,xs:12,sm:12,md:12,lg:6,xl:6,xxl:6,class:"list-col",style:{"min-height":"162px"}},{default:e(()=>[t(P,{loading:p(c),title:s.title,description:s.description,"default-value":s.enable,"action-type":s.actionType,expires:s.expires,"open-txt":n.$t("cardList.service.open"),"close-txt":n.$t("cardList.service.cancel"),"expires-text":n.$t("cardList.service.renew"),"tag-text":n.$t("cardList.service.tag"),"expires-tag-text":n.$t("cardList.service.expiresTag"),icon:s.icon},{skeleton:e(()=>[t(i,{animation:!0},{default:e(()=>[t(d,{widths:["100%","40%","100%"],rows:3}),t(d,{widths:["40%"],rows:1})]),_:1})]),_:2},1032,["loading","title","description","default-value","action-type","expires","open-txt","close-txt","expires-text","tag-text","expires-tag-text","icon"])]),_:2},1024))),128))]),_:1})])}}}),bt={class:"list-wrap"},Y=w({__name:"rules-preset",setup(o){const l=new Array(6).fill({}),{loading:c,response:u}=E(pt,l);return(n,x)=>{const m=R,d=A,i=F,y=S,v=I;return a(),f("div",bt,[t(m,{class:"block-title",heading:6},{default:e(()=>[g(_(n.$t("cardList.tab.title.preset")),1)]),_:1}),t(v,{class:"list-row",gutter:24},{default:e(()=>[(a(!0),f(k,null,N(p(u),s=>(a(),r(y,{key:s.id,xs:12,sm:12,md:12,lg:6,xl:6,xxl:6,class:"list-col",style:{"min-height":"140px"}},{default:e(()=>[t(P,{loading:p(c),title:s.title,description:s.description,"default-value":s.enable,"action-type":s.actionType,"tag-text":n.$t("cardList.preset.tag")},{skeleton:e(()=>[t(i,{animation:!0},{default:e(()=>[t(d,{widths:["100%","40%"],rows:2}),t(d,{widths:["40%"],rows:1})]),_:1})]),_:2},1032,["loading","title","description","default-value","action-type","tag-text"])]),_:2},1024))),128))]),_:1})])}}}),kt={class:"container"},wt={name:"Card"},Tt=w({...wt,setup(o){return(l,c)=>{const u=lt("Breadcrumb"),n=it,x=ct,m=S,d=rt,i=I,y=z;return a(),f("div",kt,[t(u,{items:["menu.list","menu.list.cardList"]}),t(i,{gutter:20,align:"stretch"},{default:e(()=>[t(m,{span:24},{default:e(()=>[t(y,{class:"general-card",title:l.$t("menu.list.cardList")},{default:e(()=>[t(i,{justify:"space-between"},{default:e(()=>[t(m,{span:24},{default:e(()=>[t(x,{"default-active-tab":1,type:"rounded"},{default:e(()=>[t(n,{key:"1",title:l.$t("cardList.tab.title.all")},{default:e(()=>[t(j),t(M),t(Y)]),_:1},8,["title"]),t(n,{key:"2",title:l.$t("cardList.tab.title.content")},{default:e(()=>[t(j)]),_:1},8,["title"]),t(n,{key:"3",title:l.$t("cardList.tab.title.service")},{default:e(()=>[t(M)]),_:1},8,["title"]),t(n,{key:"4",title:l.$t("cardList.tab.title.preset")},{default:e(()=>[t(Y)]),_:1},8,["title"])]),_:1})]),_:1}),t(d,{placeholder:l.$t("cardList.searchInput.placeholder"),style:{width:"240px",position:"absolute",top:"60px",right:"20px"}},null,8,["placeholder"])]),_:1})]),_:1},8,["title"])]),_:1})]),_:1})])}}});const Ht=D(Tt,[["__scopeId","data-v-e6803047"]]);export{Ht as default};
