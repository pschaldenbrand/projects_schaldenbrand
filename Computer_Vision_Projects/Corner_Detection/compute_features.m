function [features, x, y, scores] = compute_features(x, y, scores, Ix, Iy)
    e = 6;
    s = size(x,2);
    for i = 1:1:s
        if i > size(x,2)
            break
        end
        if ( (x(i) <= e) || (y(i) <= e) || ...
                (x(i) >= size(Ix,2)-e) || (y(i) >= size(Iy,1)-e) )
            x(i) = [];
            y(i) = [];
            scores(i) = [];
            s = s-1;
        end
    end


    grad_mag = [];
    grad_orient = [];
    for i = 1:1:size(Ix,1)
        for j = 1:1:size(Ix,2)
            grad_mag(i,j) = sqrt(Ix(i,j)^2 + Iy(i,j)^2);
            orient_raw = atand(Iy(i,j) / Ix(i,j));
            if( isnan(orient_raw))
                assert(grad_mag(i,j) == 0);
                orient_raw = 0; 
            end
            
            t = orient_raw + 90;
            bin = floor(t/22.5);
            if bin == 8
                bin = 7;
            end
            
            bin = bin + 1;
            grad_orient(i,j) = bin;            
        end
    end
    
    off = 5;
    features = [];
    for point = 1:1:size(x,2)
        hist = [0,0,0,0,0,0,0,0]; 
        useit = 1;
        for i = x(point)-off : 1 : x(point)+off
            for j = y(point)-off : 1 : y(point)+off
                if i > size(grad_orient,1) || j > size(grad_orient,2)
                    useit = 0;
                    continue
                end
                o = grad_orient(i,j);
                hist( o ) = hist( o ) + grad_mag(i,j);
            end
        end
        hist = hist / sum(hist);
        for i = 1:1:size(hist,2)    %clip
            if hist(i) > 0.2
                hist(i) = 0.2;
            end
        end
        hist = hist / sum(hist);
        if isnan(hist(1))
            continue
        end
        if useit
            features(point,:) = hist;
        end
    end    
    
    