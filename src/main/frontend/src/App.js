import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function App() {
  const [input, setInput] = useState('');
  const [response, setResponse] = useState('');
  const stompRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      onConnect: () => {
        client.subscribe('/topic/chat', msg => {
          const data = JSON.parse(msg.body);
          const partial = data.response_so_far || "";
          setResponse(partial);
        });
      },
      debug: str => console.log(str)
    });
    client.activate();
    stompRef.current = client;
    return () => client.deactivate();
  }, []);

  const send = () => {
    setResponse('');
    stompRef.current.publish({
      destination: '/app/chat',
      body: JSON.stringify({ type: 'chat', message: input })
    });
  };

  return (
      <div>
        <h3>사용자 입력</h3>
        <input value={input} onChange={e => setInput(e.target.value)} />
        <button onClick={send}>전송</button>
        <h3>AI 응답</h3>
        <pre>{response}</pre>
      </div>
  );
}
export default App;
