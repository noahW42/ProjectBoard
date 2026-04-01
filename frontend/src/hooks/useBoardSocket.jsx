import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

export function useBoardSocket(projectId, onEvent) {
  const clientRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS("/ws"),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("WebSocket connected");
        client.subscribe(`/topic/project/${projectId}`, (message) => {
          console.log("Raw received:", message.body);
          const event = JSON.parse(message.body);
          onEvent(event);
        });
      },
      onDisconnect: () => {
        console.log("WebSocket disconnected");
      }
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [projectId]);

  const sendEvent = (event) => {
    if (clientRef.current && clientRef.current.connected) {
      console.log("Sending event:", event);
      clientRef.current.publish({
        destination: `/app/project/${projectId}`,
        body: JSON.stringify(event)
      });
    }
  };

  return { sendEvent };
}