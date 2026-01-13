import React, { useEffect, useContext } from "react";
import { usePlaidLink } from "react-plaid-link";
import Button from "plaid-threads/Button";

import Context from "../../Context";

const Link = () => {
  const { linkToken, isPaymentInitiation, isCraProductsExclusively, dispatch } =
    useContext(Context);

  const onSuccess = React.useCallback(
    async (public_token: string) => {
        const exchangePublicTokenForAccessToken = async () => {
        const response = await fetch(
            "https://quickstart-lwsu.onrender.com/api/set_access_token",
            {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
            },
            body: `public_token=${public_token}`,
            }
        );

        if (!response.ok) {
            dispatch({
            type: "SET_STATE",
            state: {
                itemId: `no item_id retrieved`,
                accessToken: `no access_token retrieved`,
                isItemAccess: false,
            },
            });
            throw new Error("set_access_token failed");
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
        return data.access_token as string;
    };

    if (isPaymentInitiation) {
      dispatch({ type: "SET_STATE", state: { isItemAccess: false } });
    } else if (isCraProductsExclusively) {
      dispatch({ type: "SET_STATE", state: { isItemAccess: false } });
    } else {
      await exchangePublicTokenForAccessToken();
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
    <Button type="button" large onClick={() => open()} disabled={!ready}>
      Launch Link
    </Button>
  );
};

Link.displayName = "Link";

export default Link;
