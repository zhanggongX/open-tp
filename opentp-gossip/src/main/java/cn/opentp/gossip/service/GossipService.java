package cn.opentp.gossip.service;

import cn.opentp.gossip.Gossiper;
import cn.opentp.gossip.configuration.GossipProperties;
import cn.opentp.gossip.enums.ApplicationStateEnum;
import cn.opentp.gossip.gms.GossiperApp;
import cn.opentp.gossip.gms.VersionedValue;
import cn.opentp.gossip.locator.EndpointSnitch;
import cn.opentp.gossip.locator.IApplicationStateStarting;
import cn.opentp.gossip.net.MessagingService;
import cn.opentp.gossip.net.NetSnitch;
import cn.opentp.gossip.util.FBUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GossipService {

    private static Logger logger = LoggerFactory.getLogger(GossipService.class);

    static {
        GossiperApp.instance.register(new NetSnitch());
    }

    public GossiperApp getGossiper() {
        return GossiperApp.instance;
    }

    public void start(int generationNbr) throws IOException {
        //打开gossip相关的Tcp连接服务，当前项目gossip所用通信暂且独立，没用到其它通信模块。
        MessagingService.instance().listen(FBUtilities.getLocalAddress());
        logger.info("Gossiper message service has been started...");
        logger.info("Gossip starting up...");
        GossiperApp.instance.start(generationNbr);
        logger.info("Gossip has been started...");
    }


    public void stop() throws IOException, InterruptedException {
        GossiperApp.instance.stop();
        logger.info("Gossip has been stoped...");

        //关闭gossip相关的Tcp连接，当前项目gossip所用通信暂且独立，没用到其它通信模块。
        MessagingService.instance().shutdownAllConnections();
        logger.info("All Gossiper connection has been closed...");
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        Gossiper.init(new GossipProperties("localhost:9001", "localhost:9001"));

        final GossipService service = new GossipService();

        service.getGossiper().register(new EndpointSnitch());
        service.getGossiper().register(new IApplicationStateStarting() {
            public void gossiperStarting() {
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.LOAD, VersionedValue.VersionedValueFactory.instance.load(7.1));
                service.getGossiper().addLocalApplicationState(ApplicationStateEnum.WEIGHT, VersionedValue.VersionedValueFactory.instance.weight(5));

            }
        });

        service.start((int) (System.currentTimeMillis() / 1000));
    }
}