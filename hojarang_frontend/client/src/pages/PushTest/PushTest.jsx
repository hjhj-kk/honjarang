import React, { useEffect } from 'react';
import { initializeApp } from 'firebase/app';
import {
  getMessaging,
  getToken,
  onMessage,
} from 'firebase/messaging';
import axios from 'axios';

const FirebaseMessaging = () => {
  const firebaseConfig = {
    apiKey: "AIzaSyBg4MfGm1Wm4JRG9jq9MF9pHo7DuD5TLOc",
    authDomain: "honjarang-92544.firebaseapp.com",
    projectId: "honjarang-92544",
    storageBucket: "honjarang-92544.appspot.com",
    messagingSenderId: "712559736953",
    appId: "1:712559736953:web:cc25175e7bea9c4c73bca8",
    measurementId: "G-TVQ8EDTV0D"
  };
  const app = initializeApp(firebaseConfig);
  const messaging = getMessaging(app);

  useEffect(() => {
    requestPermission();

    getToken(messaging, {
      vapidKey: 'BKA1sedlFM56zEjILf13NHe4a-ovGw9B4z7VH3mSKulo-QnELaPWD8Uei_Y8b9yi_H4UfVSIE50DgM2ydgfcKd8',
    })
      .then((currentToken) => {
        if (currentToken) {
          localStorage.setItem('fcm_token', currentToken)
          console.log(currentToken);
        } else {
          console.log('No registration token available. Request permission to generate one.');
        }
      })
      .catch((err) => {
        console.log('An error occurred while retrieving token. ', err);
      });

    onMessage(messaging, (payload) => {
      console.log('Message received. ', payload);
      const { title, body } = payload.notification;
      const options = {
        body,
        icon: '/firebase-logo.png',
      };
      new Notification(title, options);
    });
  }, []);

  const requestPermission = () => {
    console.log('Requesting permission...');
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        console.log('Notification permission granted.');
      } else {
        console.log('Unable to get permission to notify.');
      }
    });
  };

  const PushTest = () => {
    const token = localStorage.getItem('fcm_token')
    const access_token = localStorage.getItem('access_token')
    axios.post('http://honjarang.kro.kr:30000/api/v1/users/fcm-token',
    {fcm_token : token},
    {headers: {
      'Authorization' : `Bearer ${access_token}`
    }})
    .then((res) => {
      console.log(res)
    })
  }


  return (
    <div>
      <header></header>
      <main>푸시테스트입니다.</main>
      <button onClick={PushTest}>푸시푸시</button>
    </div>
  );
};

export default FirebaseMessaging;
