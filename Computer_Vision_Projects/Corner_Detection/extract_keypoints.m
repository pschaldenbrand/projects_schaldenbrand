function [x, y, scores, Ix, Iy] = extract_keypoints(image)
    im = double(rgb2gray(image));
        
    window = 11;
    
    horizontal = [1,0,-1];
    vertical = [1;0;-1];
          
    Ix = double(imfilter(im, horizontal));
    Iy = double(imfilter(im, vertical));
    
    k = 0.04;
    R = double(repmat([0],size(im,1), size(im,2)));
    offset = floor(window/2);
    edge = ceil(window/2);
    for i = edge:1:size(im,1)-edge
        for j = edge:1:size(im,2)-edge
            ix = Ix(i-offset:i+offset, j-offset:j+offset);
            iy = Iy(i-offset:i+offset, j-offset:j+offset);
            t1 = ix.^2;
            t2 = ix * iy;
            t3 = iy.^2;
            m1 = sum(t1(:));
            m2 = sum(t2(:));
            m4 = sum(t3(:));
            
            M = double( [m1,m2;m2,m4] );

            R(i,j) = det(M) - k*((trace(M))^2);
        end
    end
    
    x = [];
    y = [];
    
    numPoints = floor((size(R,1) * size(R,2))/50);
    
    for i = 1:1:numPoints
        maxVal = max(max(R));
        maxInd = find( R == maxVal);
        rand = randi(size(maxInd,1));
        [I,J] = ind2sub(size(im), maxInd(rand));

        R(I,J) = -9999;
        scores(i) = maxVal;
        x(i) = J;
        y(i) = I;
    end
    
   
   