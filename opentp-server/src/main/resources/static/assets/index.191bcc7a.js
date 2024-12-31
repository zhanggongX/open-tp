import{z as j,v as E,_ as S}from"./index.8db9e24f.js";/* empty css                *//* empty css                *//* empty css                *//* empty css               */import{d as I,c as F,C as d,D as P,aI as t,aH as o,aK as G,aJ as H,aE as w,aN as c,c9 as J,ca as K,c0 as M,bc as z,j as B,aM as b,u as C,G as L,aF as T,bW as U,aY as O,bX as W,bt as X,bT as A,b1 as Y,c3 as Q,c4 as Z}from"./arco.08ce5409.js";import{u as q}from"./loading.fe93ec0f.js";/* empty css                *//* empty css                *//* empty css               *//* empty css              *//* empty css                *//* empty css               *//* empty css                *//* empty css              *//* empty css                */import"./chart.ea45cf69.js";import"./vue.74cbe595.js";function R(){return j.get("/api/profile/basic")}function D(){return j.get("/api/operation/log")}const ee={class:"item-container"},te={key:1},ae=I({__name:"profile-item",props:{type:{type:String,default:""},renderData:{type:Object,required:!0},loading:{type:Boolean,default:!1}},setup(h){const f=h,{t:a}=E.exports.useI18n(),m=F(()=>{var s,n,p,_,l,r,u,y,$,v,k,g,x,N;const{renderData:e}=f,i=[];return i.push({title:f.type==="pre"?a("basicProfile.title.preVideo"):a("basicProfile.title.video"),data:[{label:a("basicProfile.label.video.mode"),value:((s=e==null?void 0:e.video)==null?void 0:s.mode)||"-"},{label:a("basicProfile.label.video.acquisition.resolution"),value:((n=e==null?void 0:e.video)==null?void 0:n.acquisition.resolution)||"-"},{label:a("basicProfile.label.video.acquisition.frameRate"),value:`${((p=e==null?void 0:e.video)==null?void 0:p.acquisition.frameRate)||"-"} fps`},{label:a("basicProfile.label.video.encoding.resolution"),value:((_=e==null?void 0:e.video)==null?void 0:_.encoding.resolution)||"-"},{label:a("basicProfile.label.video.encoding.rate.min"),value:`${((l=e==null?void 0:e.video)==null?void 0:l.encoding.rate.min)||"-"} bps`},{label:a("basicProfile.label.video.encoding.rate.max"),value:`${((r=e==null?void 0:e.video)==null?void 0:r.encoding.rate.max)||"-"} bps`},{label:a("basicProfile.label.video.encoding.rate.default"),value:`${((u=e==null?void 0:e.video)==null?void 0:u.encoding.rate.default)||"-"} bps`},{label:a("basicProfile.label.video.encoding.frameRate"),value:`${((y=e==null?void 0:e.video)==null?void 0:y.encoding.frameRate)||"-"} fpx`},{label:a("basicProfile.label.video.encoding.profile"),value:(($=e==null?void 0:e.video)==null?void 0:$.encoding.profile)||"-"}]}),i.push({title:f.type==="pre"?a("basicProfile.title.preAudio"):a("basicProfile.title.audio"),data:[{label:a("basicProfile.label.audio.mode"),value:((v=e==null?void 0:e.audio)==null?void 0:v.mode)||"-"},{label:a("basicProfile.label.audio.acquisition.channels"),value:`${((k=e==null?void 0:e.audio)==null?void 0:k.acquisition.channels)||"-"} ${a("basicProfile.unit.audio.channels")}`},{label:a("basicProfile.label.audio.encoding.channels"),value:`${((g=e==null?void 0:e.audio)==null?void 0:g.encoding.channels)||"-"} ${a("basicProfile.unit.audio.channels")}`},{label:a("basicProfile.label.audio.encoding.rate"),value:`${((x=e==null?void 0:e.audio)==null?void 0:x.encoding.rate)||"-"} kbps`},{label:a("basicProfile.label.audio.encoding.profile"),value:((N=e==null?void 0:e.audio)==null?void 0:N.encoding.profile)||"-"}]}),i});return(e,i)=>{const s=J,n=K,p=M,_=z;return d(),P("div",ee,[t(_,{size:16,direction:"vertical",fill:""},{default:o(()=>[(d(!0),P(G,null,H(m.value,(l,r)=>(d(),w(p,{key:r,"label-style":{textAlign:"right",width:"200px",paddingRight:"10px",color:"rgb(var(--gray-8))"},"value-style":{width:"400px"},title:l.title,data:l.data},{value:o(({value:u})=>[h.loading?(d(),w(n,{key:0,animation:!0},{default:o(()=>[t(s,{widths:["200px"],rows:1})]),_:1})):(d(),P("span",te,c(u),1))]),_:2},1032,["title","data"]))),128))]),_:1})])}}});const V=S(ae,[["__scopeId","data-v-cc041ee9"]]),oe={key:0},ie={key:1},le=I({__name:"operation-log",setup(h){const{loading:f,setLoading:a}=q(!0),m=B([]);return(async()=>{try{const{data:i}=await D();m.value=i}catch{}finally{a(!1)}})(),(i,s)=>{const n=U,p=O,_=W,l=X,r=A;return d(),w(r,{class:"general-card"},{title:o(()=>[b(c(i.$t("basicProfile.title.operationLog")),1)]),default:o(()=>[t(l,{loading:C(f),style:{width:"100%"}},{default:o(()=>[t(_,{data:m.value},{columns:o(()=>[t(n,{title:i.$t("basicProfile.column.contentNumber"),"data-index":"contentNumber"},null,8,["title"]),t(n,{title:i.$t("basicProfile.column.updateContent"),"data-index":"updateContent"},null,8,["title"]),t(n,{title:i.$t("basicProfile.column.status"),"data-index":"status"},{cell:o(({record:u})=>[u.status===0?(d(),P("p",oe,[s[0]||(s[0]=L("span",{class:"circle"},null,-1)),L("span",null,c(i.$t("basicProfile.cell.auditing")),1)])):T("",!0),u.status===1?(d(),P("p",ie,[s[1]||(s[1]=L("span",{class:"circle pass"},null,-1)),L("span",null,c(i.$t("basicProfile.cell.pass")),1)])):T("",!0)]),_:1},8,["title"]),t(n,{title:i.$t("basicProfile.column.updateTime"),"data-index":"updateTime"},null,8,["title"]),t(n,{title:i.$t("basicProfile.column.operation")},{cell:o(()=>[t(p,{type:"text"},{default:o(()=>[b(c(i.$t("basicProfile.cell.view")),1)]),_:1})]),_:1},8,["title"])]),_:1},8,["data"])]),_:1},8,["loading"])]),_:1})}}});const ne=S(le,[["__scopeId","data-v-b01f27a7"]]),se={class:"container"},ce={name:"Basic"},re=I({...ce,setup(h){const{loading:f,setLoading:a}=q(!0),{loading:m,setLoading:e}=q(!0),i=B({}),s=B({}),n=B(1),p=async()=>{try{const{data:l}=await R();i.value=l,n.value=2}catch{}finally{a(!1)}},_=async()=>{try{const{data:l}=await R();s.value=l}catch{}finally{e(!1)}};return p(),_(),(l,r)=>{const u=Y("Breadcrumb"),y=O,$=z,v=Q,k=Z,g=A;return d(),P("div",se,[t(u,{items:["menu.profile","menu.profile.basic"]}),t($,{direction:"vertical",size:16,fill:""},{default:o(()=>[t(g,{class:"general-card",title:l.$t("basicProfile.title.form")},{extra:o(()=>[t($,null,{default:o(()=>[t(y,null,{default:o(()=>[b(c(l.$t("basicProfile.cancel")),1)]),_:1}),t(y,{type:"primary"},{default:o(()=>[b(c(l.$t("basicProfile.goBack")),1)]),_:1})]),_:1})]),default:o(()=>[t(k,{current:n.value,"onUpdate:current":r[0]||(r[0]=x=>n.value=x),"line-less":"",class:"steps"},{default:o(()=>[t(v,null,{default:o(()=>[b(c(l.$t("basicProfile.steps.commit")),1)]),_:1}),t(v,null,{default:o(()=>[b(c(l.$t("basicProfile.steps.approval")),1)]),_:1}),t(v,null,{default:o(()=>[b(c(l.$t("basicProfile.steps.finish")),1)]),_:1})]),_:1},8,["current"])]),_:1},8,["title"]),t(g,{class:"general-card"},{default:o(()=>[t(V,{loading:C(f),"render-data":i.value},null,8,["loading","render-data"])]),_:1}),t(g,{class:"general-card"},{default:o(()=>[t(V,{loading:C(m),type:"pre","render-data":s.value},null,8,["loading","render-data"])]),_:1}),t(ne)]),_:1})])}}});const Ce=S(re,[["__scopeId","data-v-e83af96b"]]);export{Ce as default};
