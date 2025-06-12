import React, { useState, useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const WebSocketClient = () => {
    const [type, setType] = useState('chat');
    const [message, setMessage] = useState('');
    const [responses, setResponses] = useState([]);
    const [connected, setConnected] = useState(false);
    const stompClient = useRef(null);

    useEffect(() => {
        const socket = new SockJS('http://192.168.26.165:8081/ws');
        const client = new Client({ webSocketFactory: () => socket });

        client.onConnect = () => {
            console.log('✅ STOMP 연결 성공');
            setConnected(true);

            client.subscribe('/topic/relay', msg => {
                try {
                    const data = JSON.parse(msg.body);
                    console.log('📥 응답 수신:', data);

                    if (data.done) console.log('✅ 최종 응답 도착 (done: true)');
                    if (data.image) console.log('🖼️ 이미지 포함 응답');
                    if (data.audio) console.log('🔊 오디오 포함 응답');

                    setResponses(prev => [...prev, data]);
                } catch (e) {
                    console.error('메시지 파싱 실패:', e);
                }
            });
        };

        client.activate();
        stompClient.current = client;

        return () => {
            setConnected(false);
            client.deactivate();
        };
    }, []);

    const handleSend = () => {
        if (!type || !message) return;

        const payload = { type, message };

        if (stompClient.current && stompClient.current.connected) {
            console.log('🚀 React → Spring 전송:', payload);
            stompClient.current.publish({
                destination: '/app/relay',
                body: JSON.stringify(payload)
            });
            setResponses([]);
        } else {
            console.warn('❌ STOMP 연결되지 않음. 잠시 후 다시 시도하세요.');
        }
    };

    return (
        <div style={{ padding: '1rem' }}>
            <h2>AI 요청</h2>

            <label>
                타입 선택:
                <select value={type} onChange={e => setType(e.target.value)}>
                    <option value="chat">chat</option>
                    <option value="generate_image">generate_image</option>
                    <option value="generate_tts">generate_tts</option>
                    <option value="ping">ping</option>
                </select>
            </label>

            <br /><br />

            <label>
                메시지 입력:
                <input
                    type="text"
                    value={message}
                    onChange={e => setMessage(e.target.value)}
                    style={{ width: '300px' }}
                />
            </label>

            <br /><br />
            <button onClick={handleSend} disabled={!connected}>
                {connected ? '전송' : '연결 중...'}
            </button>

            <h3>응답:</h3>
            <div style={{ background: '#f0f0f0', padding: '1rem' }}>
                {responses.map((res, idx) => (
                    <div key={idx} style={{ marginBottom: '1rem' }}>
                        {res.image && (
                            <div>
                                <p>🔹 이미지:</p>
                                <img src={`data:image/png;base64,${res.image}`} alt="AI 생성 이미지" style={{ maxWidth: '100%' }} />
                            </div>
                        )}
                        {res.audio && (
                            <div>
                                <p>🔊 오디오:</p>
                                <audio controls src={`data:audio/wav;base64,${res.audio}`}></audio>
                            </div>
                        )}
                        <pre style={{ whiteSpace: 'pre-wrap' }}>{JSON.stringify(res, null, 2)}</pre>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default WebSocketClient;
