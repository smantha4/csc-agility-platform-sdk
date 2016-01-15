package com.servicemesh.core.async.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.servicemesh.core.async.AsyncService;
import com.servicemesh.core.async.RequestHandler;
import com.servicemesh.core.async.ResponseHandler;
import com.servicemesh.core.collections.hash.HashMapLongG;
import com.servicemesh.core.messaging.Request;
import com.servicemesh.core.messaging.SleepReq;
import com.servicemesh.core.messaging.SleepResp;
import com.servicemesh.core.messaging.Status;
import com.servicemesh.core.reactor.Reactor;
import com.servicemesh.core.reactor.Timer;
import com.servicemesh.core.reactor.TimerHandler;
import com.servicemesh.core.reactor.TimerReactor;

public class SleeperService extends AsyncService
{
    /** Logger for this class. */
    private final static Logger s_logger = Logger.getLogger(SleeperService.class);

    public SleeperService(Reactor reactor)
    {
        super(reactor);
        registerRequest(SleepReq.class, new RequestHandler<SleepReq>() {
            HashMapLongG<Timer> timers = new HashMapLongG<Timer>();

            @Override
            public void onRequest(final SleepReq req)
            {
                long delay = req.getDelay();
                Timer timer = _reactor.timerCreateRel(delay, new TimerHandler() {
                    @Override
                    public long timerFire(long scheduledTime, long actualTime)
                    {
                        SleepResp resp = new SleepResp();
                        resp.setText(req.getText());
                        resp.setStatus(Status.COMPLETE);
                        sendResponse(req, resp);
                        return req.isRepeat() ? System.currentTimeMillis() + req.getDelay() : 0L;
                    }
                });
                timers.put(req.getReqId(), timer);
            }

            @Override
            public void onCancel(long reqId)
            {
                int entry = timers.getEntry(reqId);
                if (entry != -1)
                {
                    Timer timer = timers.getEntryValue(entry);
                    timer.cancel();
                    timers.removeEntry(entry);
                }
                else
                {
                    s_logger.error("Canceled unknown request: " + reqId);
                }
            }
        });
    }

    public static void usage()
    {
        System.out.println("usage:\n" + "sleep [repeat] <millis> <millis> <millis>...\n" + "cancel <id> <id>...");
    }

    public static void main(String[] args)
    {
        TimerReactor reactor = TimerReactor.getTimerReactor("SleeperReactor");
        SleeperService svc = new SleeperService(reactor);

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String line;
        try
        {
            for (System.out.print("> "); (line = br.readLine()) != null; System.out.print("> "))
            {
                String[] tokens = line.split("[ \t\n]");
                long delay = 0L;

                if (tokens.length < 1)
                {
                    usage();
                    continue;
                }

                String command = tokens[0];
                if (command.equals("sleep"))
                {
                    final boolean repeat = tokens.length >= 2 && tokens[1].equals("repeat");
                    for (int i = repeat ? 2 : 1; i < tokens.length; i++)
                    {
                        delay = Long.parseLong(tokens[i]);
                        SleepReq sr = new SleepReq();
                        sr.setDelay(delay);
                        sr.setText(line + "(" + i + ")");
                        sr.setUser("John Catalano");
                        sr.setRepeat(repeat);
                        svc.sendRequest(sr, new ResponseHandler<SleepResp>() {
                            @Override
                            public boolean onResponse(SleepResp resp)
                            {
                                s_logger.info("SleepResp received:" + resp.getReqId() + ":" + resp.getText());
                                return repeat;
                            }

                            @Override
                            public void onError(Request request, Throwable t)
                            {
                                s_logger.error(request.getReqId() + ":" + t.getMessage());
                            }
                        });
                    }
                }
                else if (command.equals("cancel"))
                {
                    for (int i = 1; i < tokens.length; i++)
                    {
                        long reqId = Long.parseLong(tokens[i]);
                        svc.cancelRequest(reqId);
                    }
                }
                else
                {
                    usage();
                }
            }
        }
        catch (Throwable t)
        {
            System.out.println(t);
        }
        reactor.shutdown();
        System.out.println();
    }
}
