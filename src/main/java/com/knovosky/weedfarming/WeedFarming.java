/*
 * Copyright 2020 Tim De Smet (Knovosky, knovosky.com)
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
 */
package com.knovosky.weedfarming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeedFarming {

    private final static Logger LOG = LoggerFactory.getLogger("Init");

    public static void main(String[] args)
    {
        if(args.length==0)
        {
            LOG.error("Must include command line arguments");
        }
        else try
        {
            switch(args[0])
            {
                case "bot":
                    Bot.main(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args.length>3 ? Integer.parseInt(args[3]) : 32);
                    break;
                case "none":
                    break;
                default:
                    LOG.error(String.format("Invalid startup type '%s'",args[0]));
            }
        }
        catch (Exception e)
        {
            LOG.error(""+e);
            e.printStackTrace();
        }
    }
}
