package com.pubkit;

import com.pubkit.service.BasePubKitClient;
import com.pubkit.service.PubKitClient;

/**
 * Created by puran on 3/22/15.
 */
public final class PubKit {

    private static BasePubKitClient INSTANCE;

    /**
     * Initializes the PubKit client. User {@link https://pubkit.co} to register the PubKit app
     * and get applicationId, applicationKey and applicationSecret.
     *
     * @param applicationId the PubKit applicationId
     * @param applicationKey the PubKit application key
     * @param applicationSecret the PubKit application secret
     *
     * @return PubKit client object to perform all messaging operations
     */
    public static PubKitClient initPubKitClient(String applicationId, String applicationKey, String applicationSecret) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new BasePubKitClient(applicationId, applicationKey, applicationSecret);
        return INSTANCE;
    }

    public static PubKitClient getInstance() {
        return INSTANCE;
    }
}

