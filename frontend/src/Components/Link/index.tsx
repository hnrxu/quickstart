import React, { useEffect, useContext } from "react";
import { usePlaidLink } from "react-plaid-link";
import Button from "plaid-threads/Button";
import styles from "./index.module.scss";

import Context from "../../Context";

const Link = () => {
  const { linkToken, isPaymentInitiation, isCraProductsExclusively, dispatch } =
    useContext(Context);

  const onSuccess = React.useCallback(
    (public_token: string, metadata: any) => {
        console.log("🔥 onSuccess CALLED");
        console.log("token:", public_token);
        console.log("metadata:", metadata);
        console.log("metadata account:", metadata.accounts);
      // If the access_token is needed, send public_token to server
      const exchangePublicTokenForAccessToken = async () => {
        // check this
        const response = await fetch("https://quickstart-lwsu.onrender.com/api/set_access_token", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          },
          body: `public_token=${public_token}`,
        });
        if (!response.ok) {
          dispatch({
            type: "SET_STATE",
            state: {
              itemId: `no item_id retrieved`,
              accessToken: `no access_token retrieved`,
              isItemAccess: false,
            },
          });
          return;
        }
        const data = await response.json();
        dispatch({
          type: "SET_STATE",
          state: {
            itemId: data.item_id,
            accessToken: data.access_token,
            isItemAccess: true,
          },
        });
      };

      // 'payment_initiation' products do not require the public_token to be exchanged for an access_token.
      if (isPaymentInitiation) {
        dispatch({ type: "SET_STATE", state: { isItemAccess: false } });
      } else if (isCraProductsExclusively) {
        // When only CRA products are enabled, only user_token is needed. access_token/public_token exchange is not needed.
        dispatch({ type: "SET_STATE", state: { isItemAccess: false } });
      } else {
        exchangePublicTokenForAccessToken();
      }

      dispatch({ type: "SET_STATE", state: { linkSuccess: true } });
      window.history.pushState("", "", "/");
    },
    [dispatch, isPaymentInitiation, isCraProductsExclusively]
  );

  let isOauth = false;
  const config: Parameters<typeof usePlaidLink>[0] = {
    token: linkToken!,
    onSuccess,
    onExit: (err, metadata) => {
    console.log("PLAID onExit err:", err);
    console.log("PLAID onExit metadata:", metadata);
  },
  onEvent: (eventName, metadata) => {
    console.log("PLAID onEvent:", eventName, metadata);
  },
  };

  if (window.location.href.includes("?oauth_state_id=")) {
    // TODO: figure out how to delete this ts-ignore
    // @ts-ignore
    config.receivedRedirectUri = window.location.href;
    isOauth = true;
  }

  const { open, ready } = usePlaidLink(config);

  useEffect(() => {
    if (isOauth && ready) {
      open();
    }
  }, [ready, open, isOauth]);

  return (

    <button type="button" onClick={() => open()} disabled={!ready}>
    Connect Bank
    </button>


  );
};

Link.displayName = "Link";

export default Link;
